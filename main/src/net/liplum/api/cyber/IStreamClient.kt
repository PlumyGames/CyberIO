package net.liplum.api.cyber

import arc.graphics.Color
import arc.struct.ObjectSet
import arc.struct.Seq
import mindustry.type.Liquid
import net.liplum.api.ICyberEntity
import net.liplum.common.delegates.Delegate1
import net.liplum.mdt.CalledBySync
import net.liplum.mdt.ClientOnly

interface IStreamClient : ICyberEntity {
    fun readStream(host: IStreamHost, liquid: Liquid, amount: Float)
    /**
     * Gets the max acceptable number of this `liquid`.
     * negative number means any
     *
     * @param host   host
     * @param liquid liquid
     * @return amount
     */
    fun acceptedAmount(host: IStreamHost, liquid: Liquid): Float
    val onRequirementUpdated: Delegate1<IStreamClient>
    /**
     * Gets what this client wants<br></br>
     * null : Any<br></br>
     * Array.Empty : Nothing<br></br>
     * An seq : all things in the array<br></br>
     * Please cache this value, this is a mutable list.
     * @return what this client wants
     */
    val requirements: Seq<Liquid>?
    @CalledBySync
    fun connect(host: IStreamHost) {
        connectedHosts.add(host.building.pos())
    }
    @CalledBySync
    fun disconnect(host: IStreamHost) {
        connectedHosts.remove(host.building.pos())
    }

    val connectedHosts: ObjectSet<Int>
    fun isConnectedWith(host: IStreamHost): Boolean {
        return connectedHosts.contains(host.building.pos())
    }
    /**
     * Gets the maximum limit of connection.<br></br>
     * -1 : unlimited
     *
     * @return the maximum of connection
     */
    val maxHostConnection: Int
    fun acceptConnection(host: IStreamHost): Boolean {
        return canHaveMoreHostConnection()
    }

    fun canHaveMoreHostConnection(): Boolean {
        val max = maxHostConnection
        return if (max == -1) {
            true
        } else connectedHosts.size < max
    }

    val hostConnectionNumber: Int
        get() = connectedHosts.size
    @ClientOnly
    val clientColor: Color
}