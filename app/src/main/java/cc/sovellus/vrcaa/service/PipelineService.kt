package cc.sovellus.vrcaa.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.api.PipelineContext
import cc.sovellus.vrcaa.api.models.Friends
import cc.sovellus.vrcaa.api.models.pipeline.FriendLocation
import cc.sovellus.vrcaa.api.models.pipeline.FriendOffline
import cc.sovellus.vrcaa.api.models.pipeline.FriendOnline
import com.google.gson.Gson

class PipelineService : Service() {

    private var pipeline: PipelineContext? = null
    private var friends: ArrayList<FriendOnline.User> = arrayListOf()
    // Passed from Intent
    private lateinit var activeFriends: Friends

    private val listener = object : PipelineContext.SocketListener {
        override fun onMessage(message: Any?) {
            if (message != null) {
                serviceHandler?.obtainMessage()?.also { msg ->
                    msg.obj = message
                    serviceHandler?.sendMessage(msg)
                }
            }
        }

    }

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    private var notificationCounter: Int = 0

    private fun pushNotification(
        title: String,
        content: String
    ) {
        val builder = NotificationCompat.Builder(this, App.CHANNEL_ID)
            .setSmallIcon(androidx.core.R.drawable.notification_icon_background)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.FLAG_ONGOING_EVENT)

        val notificationManager = NotificationManagerCompat.from(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(notificationCounter, builder.build())
        }
    }

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            when (msg.obj) {
                is FriendOnline -> {
                    val friend = msg.obj as FriendOnline

                    // We don't want to add duplicates to the list.
                    if (friends.find { it.id == friend.userId } == null)
                        friends.add(friend.user)

                    pushNotification(
                        title = "Friend has come online!",
                        content = "Your friend, ${friend.user.displayName} has come online!"
                    )

                    notificationCounter++
                }
                is FriendOffline -> {
                    val friend = msg.obj as FriendOffline

                    // Remove from list if found.
                    val friendObject = friends.find { it.id == friend.userId }
                    if (friendObject != null)
                    {
                        pushNotification(
                            title = "Friend has gone offline!",
                            content = "Your friend, ${friendObject.displayName} has gone offline!"
                        )

                        friends = friends.filter { it.id != friend.userId } as ArrayList<FriendOnline.User>
                    } else {
                        // It seems it was not cached during local session, instead fallback to currentFriends
                        // That we pass from the Intent to this Service
                        val fallbackFriend = activeFriends.find { it.id == friend.userId }

                        pushNotification(
                            title = "Friend has gone offline!",
                            content = "Your friend, ${fallbackFriend?.displayName} has gone offline!"
                        )

                        activeFriends = activeFriends.filter { it.id != friend.userId } as Friends
                    }

                    notificationCounter++
                }
                is FriendLocation -> {
                    val friend = msg.obj as FriendLocation

                    // if "friend.travelingToLocation" is not empty, it means friend is currently travelling.
                    // We want to show it only once, so only show when the travelling is done.
                    if (friend.travelingToLocation.isEmpty()) {
                        pushNotification(
                            title = "Friend changed location!",
                            content = "Your friend, ${friend.user.displayName} went to ${friend.world.name}."
                        )
                    }

                    notificationCounter++
                }
                else -> { /* Not Implemented */ }
            }
        }
    }


    override fun onCreate() {
        HandlerThread("ServiceStartArguments", 10).apply {
            start()

            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this, "PipelineService started, listening for events.", Toast.LENGTH_SHORT).show()

        this.pipeline = PipelineContext(intent.getStringExtra("access_token")!!)
        this.activeFriends = Gson().fromJson(intent.getStringExtra("online_friends")!!, Friends::class.java)

        pipeline.let { pipeline ->
            pipeline!!.connect()
            listener.let { pipeline.setListener(it) }
        }

        return START_STICKY // This makes sure, the service does not get killed.
    }

    override fun onDestroy() {
        Toast.makeText(this, "PipelineService got killed, uh oh!", Toast.LENGTH_SHORT).show()
        pipeline!!.disconnect()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}