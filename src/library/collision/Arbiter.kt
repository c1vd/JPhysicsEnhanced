package library.collision

import library.dynamics.Body
import library.dynamics.Settings
import library.geometry.Circle
import library.geometry.Polygon
import library.math.Vec2
import kotlin.math.min


/**
 * Creates manifolds to detect collisions and apply forces to them. Discrete in nature and only evaluates pairs of bodies in a single manifold.
 */
class Arbiter(val a: Body, val b: Body) {
    /**
     * Static fiction constant to be set during the construction of the arbiter.
     */
    var staticFriction: Double = (a.staticFriction + b.staticFriction) / 2

    /**
     * Dynamic fiction constant to be set during the construction of the arbiter.
     */
    var dynamicFriction: Double = (a.dynamicFriction + b.dynamicFriction) / 2

    /**
     * Array to save the contact points of the objects body's in world space.
     */
    val contacts: Array<Vec2> = arrayOf(Vec2(), Vec2())
    var contactNormal: Vec2 = Vec2()
    var contactCount: Int = 0
    var restitution: Double = 0.0

    /**
     * Conducts a narrow phase detection and creates a contact manifold.
     */
    fun narrowPhase() {
        restitution = min(a.restitution, b.restitution)
        if (a.shape is Circle && b.shape is Circle) {
            circleVsCircle()
        } else if (a.shape is Circle && b.shape is Polygon) {
            circleVsPolygon(this.a, this.b)
        } else if (a.shape is Polygon && b.shape is Circle) {
            circleVsPolygon(this.b, this.a)
            if (this.contactCount > 0) {
                this.contactNormal.negative()
            }
        } else if (a.shape is Polygon && b.shape is Polygon) {
            polygonVsPolygon()
        }
    }

    private var penetration = 0.0

    /**
     * Circle vs circle collision detection method
     */
    private fun circleVsCircle() {
        val ca = a.shape as Circle
        val cb = b.shape as Circle

        val normal = b.position - a.position

        val distance = normal.length
        val radius = ca.radius + cb.radius

        if (distance >= radius) {
            contactCount = 0
            return
        }

        this.contactCount = 1

        if (distance == 0.0) {
            this.penetration = radius
            this.contactNormal = Vec2(0.0, 1.0)
            this.contacts[0].set(a.position)
        } else {
            this.penetration = radius - distance
            this.contactNormal = normal.normalized
            this.contacts[0].set(this.contactNormal * ca.radius + a.position)
        }
    }

    /**
     * Circle vs Polygon collision detection method
     *
     * @param a Circle object
     * @param b Polygon Object
     */
    private fun circleVsPolygon(a: Body, b: Body) {
        val A = a.shape as Circle
        val B = b.shape as Polygon

        //Transpose effectively removes the rotation thus allowing the OBB vs OBB detection to become AABB vs OBB
        val distOfBodies = a.position - b.position
        val polyToCircleVec = B.orient.transpose().mul(distOfBodies)
        var penetration = -Double.Companion.MAX_VALUE
        var faceNormalIndex = 0

        //Applies SAT to check for potential penetration
        //Retrieves best face of polygon
        for (i in B.vertices.indices) {
            val v = polyToCircleVec - B.vertices[i]
            val distance = B.normals[i].dotProduct(v)

            //If circle is outside of polygon, no collision detected.
            if (distance > A.radius) {
                return
            }

            if (distance > penetration) {
                faceNormalIndex = i
                penetration = distance
            }
        }

        //Get vertex's of best face
        val vector1 = B.vertices[faceNormalIndex]
        val vector2 = B.vertices[if (faceNormalIndex + 1 < B.vertices.size) faceNormalIndex + 1 else 0]

        val v1ToV2 = vector2 - vector1
        val circleBodyTov1 = polyToCircleVec - vector1
        val firstPolyCorner = circleBodyTov1.dotProduct(v1ToV2)

        //If first vertex is positive, v1 face region collision check
        if (firstPolyCorner <= 0.0) {
            val distBetweenObj = polyToCircleVec.distance(vector1)

            //Check to see if vertex is within the circle
            if (distBetweenObj >= A.radius) {
                return
            }

            this.penetration = A.radius - distBetweenObj
            contactCount = 1
            B.orient.mul(this.contactNormal.set(vector1 - polyToCircleVec).normalize())
            contacts[0] = B.orient.mul(vector1, Vec2()) + b.position
            return
        }

        val v2ToV1 = vector1 - vector2
        val circleBodyTov2 = polyToCircleVec - vector2
        val secondPolyCorner = circleBodyTov2.dotProduct(v2ToV1)

        //If second vertex is positive, v2 face region collision check
        //Else circle has made contact with the polygon face.
        if (secondPolyCorner < 0.0) {
            val distBetweenObj = polyToCircleVec.distance(vector2)

            //Check to see if vertex is within the circle
            if (distBetweenObj >= A.radius) {
                return
            }

            this.penetration = A.radius - distBetweenObj
            contactCount = 1
            B.orient.mul(this.contactNormal.set((vector2 - polyToCircleVec).normalize()))
            contacts[0] = B.orient.mul(vector2, Vec2()) + b.position
        } else {
            val distFromEdgeToCircle = (polyToCircleVec - vector1).dotProduct(B.normals[faceNormalIndex])

            if (distFromEdgeToCircle >= A.radius) {
                return
            }

            this.penetration = A.radius - distFromEdgeToCircle
            this.contactCount = 1
            B.orient.mul(B.normals[faceNormalIndex], this.contactNormal)
            val circleContactPoint = a.position + this.contactNormal.negative() * A.radius
            this.contacts[0].set(circleContactPoint)
        }
    }

    /**
     * Polygon collision check
     */
    private fun polygonVsPolygon() {
        val pa = a.shape as Polygon
        val pb = b.shape as Polygon

        val aData = AxisData()
        findAxisOfMinPenetration(aData, pa, pb)
        if (aData.penetration >= 0) {
            return
        }

        val bData = AxisData()
        findAxisOfMinPenetration(bData, pb, pa)
        if (bData.penetration >= 0) {
            return
        }

        val referenceFaceIndex: Int
        val referencePoly: Polygon?
        val incidentPoly: Polygon?
        val flip: Boolean

        if (selectionBias(aData.penetration, bData.penetration)) {
            referencePoly = pa
            incidentPoly = pb
            referenceFaceIndex = aData.referenceFaceIndex
            flip = false
        } else {
            referencePoly = pb
            incidentPoly = pa
            referenceFaceIndex = bData.referenceFaceIndex
            flip = true
        }


        var referenceNormal = referencePoly.normals[referenceFaceIndex]

        //Reference face of reference polygon in object space of incident polygon
        referenceNormal = referencePoly.orient.mul(referenceNormal, Vec2())
        referenceNormal = incidentPoly.orient.transpose().mul(referenceNormal, Vec2())

        //Finds face of incident polygon angled best vs reference poly normal.
        //Best face is the incident face that is the most anti parallel (most negative dot product)
        var incidentIndex = 0
        var minDot = Double.Companion.MAX_VALUE
        for (i in incidentPoly.vertices.indices) {
            val dot = referenceNormal.dotProduct(incidentPoly.normals[i])

            if (dot < minDot) {
                minDot = dot
                incidentIndex = i
            }
        }

        //Incident faces vertexes in world space
        val incidentFaceVertexes = arrayOf(
            incidentPoly.orient.mul(incidentPoly.vertices[incidentIndex], Vec2()) + incidentPoly.body.position,
            incidentPoly.orient.mul(
                incidentPoly.vertices[if (incidentIndex + 1 >= incidentPoly.vertices.size) 0 else incidentIndex + 1],
                Vec2()
            ) + incidentPoly.body.position
        )


        //Gets vertex's of reference polygon reference face in world space
        var v1 = referencePoly.vertices[referenceFaceIndex]
        var v2 =
            referencePoly.vertices[if (referenceFaceIndex + 1 == referencePoly.vertices.size) 0 else referenceFaceIndex + 1]

        //Rotate and translate vertex's of reference poly
        v1 = referencePoly.orient.mul(v1, Vec2()) + referencePoly.body.position
        v2 = referencePoly.orient.mul(v2, Vec2()) + referencePoly.body.position

        val refTangent = v2 - v1
        refTangent.normalize()

        val negSide = -refTangent.dotProduct(v1)
        val posSide = refTangent.dotProduct(v2)
        // Clips the incident face against the reference
        var np = clip(-refTangent, negSide, incidentFaceVertexes)

        if (np < 2) {
            return
        }

        np = clip(refTangent, posSide, incidentFaceVertexes)

        if (np < 2) {
            return
        }

        val refFaceNormal = -refTangent.normal()

        val contactVectorsFound: Array<Vec2> = arrayOf(Vec2(0.0, 0.0), Vec2(0.0, 0.0))
        var totalPen = 0.0
        var contactsFound = 0

        //Discards points that are positive/above the reference face
        for (i in 0..1) {
            val separation = refFaceNormal.dotProduct(incidentFaceVertexes[i]) - refFaceNormal.dotProduct(v1)
            if (separation <= 0.0 + Settings.EPSILON) {
                contactVectorsFound[contactsFound] = incidentFaceVertexes[i]
                totalPen += -separation
                contactsFound++
            }
        }

        val contactPoint: Vec2
        if (contactsFound == 1) {
            contactPoint = contactVectorsFound[0]
            this.penetration = totalPen
        } else {
            contactPoint = (contactVectorsFound[1] + contactVectorsFound[0]) * 0.5
            this.penetration = totalPen / 2
        }
        this.contactCount = 1
        this.contacts[0].set(contactPoint)
        contactNormal.set(if (flip) refFaceNormal.negative() else refFaceNormal)
    }

    /**
     * Clipping for polygon collisions. Clips incident face against side planes of the reference face.
     *
     * @param planeTangent Plane to clip against
     * @param offset       Offset for clipping in world space to incident face.
     * @param incidentFace Clipped face vertex's
     * @return Number of clipped vertex's
     */
    private fun clip(planeTangent: Vec2, offset: Double, incidentFace: Array<Vec2>): Int {
        var num = 0
        val out = arrayOf<Vec2>(
            Vec2(incidentFace[0]),
            Vec2(incidentFace[1])
        )
        val dist = planeTangent.dotProduct(incidentFace[0]) - offset
        val dist1 = planeTangent.dotProduct(incidentFace[1]) - offset

        if (dist <= 0.0) out[num++].set(incidentFace[0])
        if (dist1 <= 0.0) out[num++].set(incidentFace[1])

        if (dist * dist1 < 0.0) {
            val interp = dist / (dist - dist1)

            out[num].set((incidentFace[1] - incidentFace[0]) * interp + incidentFace[0])
            num++
        }

        incidentFace[0] = out[0]
        incidentFace[1] = out[1]

        return num
    }

    /**
     * Finds the incident face of polygon A in object space relative to polygons B position.
     *
     * @param data Data obtained from earlier penetration test.
     * @param A    Polygon A to test.
     * @param B    Polygon B to test.
     */
    fun findAxisOfMinPenetration(data: AxisData, A: Polygon, B: Polygon) {
        var distance = -Double.Companion.MAX_VALUE
        var bestIndex = 0

        for (i in A.vertices.indices) {
            //Applies polygon A's orientation to its normals for calculation.
            val polyANormal = A.orient.mul(A.normals[i], Vec2())

            //Rotates the normal by the clock wise rotation matrix of B to put the normal relative to the object space of polygon B
            //Polygon b is axis aligned and the normal is located according to this in the correct position in object space
            val objectPolyANormal = B.orient.transpose().mul(polyANormal, Vec2())

            var bestProjection = Double.Companion.MAX_VALUE
            var bestVertex = B.vertices[0]

            //Finds the index of the most negative vertex relative to the normal of polygon A
            for (x in B.vertices.indices) {
                val vertex = B.vertices[x]
                val projection = vertex.dotProduct(objectPolyANormal)

                if (projection < bestProjection) {
                    bestVertex = vertex
                    bestProjection = projection
                }
            }

            //Distance of B to A in world space space
            val distanceOfBA = A.body.position - B.body.position

            //Best vertex relative to polygon B in object space
            val polyANormalVertex = B.orient.transpose().mul(A.orient.mul(A.vertices[i], Vec2()) + distanceOfBA)

            //Distance between best vertex and polygon A's plane in object space
            val d = objectPolyANormal.dotProduct(bestVertex - polyANormalVertex)

            //Records penetration and vertex
            if (d > distance) {
                distance = d
                bestIndex = i
            }
        }
        data.penetration = distance
        data.referenceFaceIndex = bestIndex
    }

    /**
     * Resolves any penetrations that are left overlapping between shapes. This can be cause due to integration errors of the solvers integration method.
     * Based on linear projection to move the shapes away from each other based on a correction constant and scaled relative to the inverse mass of the objects.
     */
    fun penetrationResolution() {
        val penetrationTolerance = penetration - Settings.PENETRATION_ALLOWANCE

        if (penetrationTolerance <= 0.0) {
            return
        }

        val totalMass = a.mass + b.mass
        val correction = (penetrationTolerance * Settings.PENETRATION_CORRECTION) / totalMass
        a.position += -contactNormal * a.mass * correction
        b.position += contactNormal * b.mass * correction
    }

    /**
     * Solves the current contact manifold and applies impulses based on any contacts found.
     */
    fun solve() {
        val contactA = contacts[0] - a.position
        val contactB = contacts[0] - b.position

        //Relative velocity created from equation found in GDC talk of box2D lite.
        var relativeVel = b.velocity + contactB.cross(b.angularVelocity) - a.velocity -
                contactA.cross(
                    a.angularVelocity
                )


        //Positive = converging Negative = diverging
        val contactVel = relativeVel.dotProduct(contactNormal)

        //Prevents objects colliding when they are moving away from each other.
        //If not, objects could still be overlapping after a contact has been resolved and cause objects to stick together
        if (contactVel >= 0) {
            return
        }

        val acn = contactA.cross(contactNormal)
        val bcn = contactB.cross(contactNormal)
        val inverseMassSum = a.invMass + b.invMass + (acn * acn) * a.invI + (bcn * bcn) * b.invI

        var j = -(restitution + 1) * contactVel
        j /= inverseMassSum

        val impulse = contactNormal * j
        b.applyLinearImpulse(impulse, contactB)
        a.applyLinearImpulse(-impulse, contactA)

        relativeVel = b.velocity + contactB.cross(b.angularVelocity) - a.velocity - contactA.cross(
            a.angularVelocity
        )


        val t = (relativeVel.copy() - contactNormal * relativeVel.dotProduct(contactNormal)).normalized

        var jt = -relativeVel.dotProduct(t)
        jt /= inverseMassSum

        val tangentImpulse: Vec2 = if (StrictMath.abs(jt) < j * staticFriction) {
            t * jt
        } else {
            -t * j * dynamicFriction
        }

        b.applyLinearImpulse(tangentImpulse, contactB)
        a.applyLinearImpulse(-tangentImpulse, contactA)
    }

    companion object {
        /**
         * Method to check if point is inside a body in world space.
         *
         * @param b          Body to check against.
         * @param startPoint Vector point to check if its inside the first body.
         * @return boolean value whether the point is inside the first body.
         */
        @JvmStatic
        fun isPointInside(b: Body, startPoint: Vec2): Boolean {
            if (b.shape is Polygon) {
                val poly = b.shape as Polygon
                for (i in poly.vertices.indices) {
                    val objectPoint = startPoint - poly.body.position + poly.body.shape.orient.mul(
                        poly.vertices[i],
                        Vec2()
                    )


                    if (objectPoint.dotProduct(poly.body.shape.orient.mul(poly.normals[i], Vec2())) > 0) {
                        return false
                    }
                }
            } else if (b.shape is Circle) {
                val circle = b.shape as Circle
                val d = b.position - startPoint

                return !(d.length > circle.radius)
            }

            return true
        }

        /**
         * Selects one value over another. Intended for polygon collisions to aid in choosing which axis of separation intersects the other in a consistent manner.
         * Floating point error can occur in the rotation calculations thus this method helps with choosing one axis over another in a consistent manner for stability.
         *
         * @param a penetration value a
         * @param b penetration value b
         * @return boolean value whether a is to be preferred or not.
         */
        private fun selectionBias(a: Double, b: Double): Boolean {
            return a >= b * Settings.BIAS_RELATIVE + a * Settings.BIAS_ABSOLUTE
        }
    }
}

