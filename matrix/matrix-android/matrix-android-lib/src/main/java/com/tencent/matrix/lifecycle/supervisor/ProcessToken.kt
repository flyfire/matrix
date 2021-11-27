package com.tencent.matrix.lifecycle.supervisor

import android.content.Context
import android.os.*
import com.tencent.matrix.lifecycle.owners.StagedBackgroundOwner
import com.tencent.matrix.util.MatrixUtil

/**
 * Created by Yves on 2021/11/11
 */
class ProcessToken : Parcelable {
    val binder: IBinder
    val pid: Int
    val name: String
    val staged: Boolean
    val statefulName: String

    companion object {

        @JvmStatic
        fun current(context: Context, statefulName: String = "") = ProcessToken(
            Process.myPid(),
            MatrixUtil.getProcessName(context),
            StagedBackgroundOwner.active(),
            statefulName
        )

        @JvmField
        val CREATOR: Parcelable.Creator<ProcessToken> = object : Parcelable.Creator<ProcessToken> {
            override fun createFromParcel(src: Parcel): ProcessToken {
                return ProcessToken(src)
            }

            override fun newArray(size: Int): Array<ProcessToken?> {
                return arrayOfNulls(size)
            }
        }
    }

    constructor(pid: Int, processName: String, staged: Boolean, statefulName: String) {
        this.binder = Binder()
        this.pid = pid
        this.name = processName
        this.staged = staged
        this.statefulName = statefulName
    }

    constructor(src: Parcel) {
        this.binder = src.readStrongBinder()
        this.pid = src.readInt()
        this.name = src.readString() ?: ""
        this.staged = src.readInt() != 0
        this.statefulName = src.readString() ?: ""
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeStrongBinder(binder)
        dest.writeInt(pid)
        dest.writeString(name)
        dest.writeInt(if (staged) 1 else 0)
        dest.writeString(statefulName)
    }

    fun linkToDeath(recipient: IBinder.DeathRecipient) {
        binder.linkToDeath(recipient, 0)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other !is ProcessToken) {
            return false
        }
        return name == other.name && pid == other.pid
    }

    override fun hashCode(): Int {
        var result = pid
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "ProcessToken(pid=$pid, name='$name', staged=$staged, statefulName = $statefulName)"
    }
}