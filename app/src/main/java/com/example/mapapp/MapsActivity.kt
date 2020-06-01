package com.example.mapapp

//import android.R

//import android.util.Log.*
//import com.google.android.material.internal.ContextUtils.getActivity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.BounceInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.android.material.navigation.NavigationView

//
//fun screenWidth(context: Context): Int {
//    val wm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//    val display: Display = wm.getDefaultDisplay()
//    return display.getWidth()
//}
//fun getHeight(t: TextView): Int {
//    val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
//        screenWidth(t.context),
//        View.MeasureSpec.AT_MOST
//    )
//    val heightMeasureSpec =
//        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//    t.measure(widthMeasureSpec, heightMeasureSpec)
//    return t.measuredHeight
//}
fun getBitmapFromVector(
    context: Context,
    vectorResourceId: Int,
    tintColor: Int
): BitmapDescriptor? {
    val vectorDrawable: Drawable = ResourcesCompat.getDrawable( context.resources
                                                              , vectorResourceId
                                                              , null
    ) ?: return null
    val bitmap: Bitmap = Bitmap.createBitmap( vectorDrawable.intrinsicWidth
                                            , vectorDrawable.intrinsicHeight
                                            , Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    DrawableCompat.setTint(vectorDrawable, tintColor)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun log(msg:String) { Log.i("XXX", msg) }


class MapsActivity
    : AppCompatActivity()
    , OnMapReadyCallback
    , GoogleMap.OnInfoWindowClickListener
  //  , GoogleMap.OnInfoWindowLongClickListener
    , GoogleMap.OnMapClickListener
    , OnMarkerClickListener
    , NavigationView.OnNavigationItemSelectedListener
    , GoogleMap.InfoWindowAdapter
{
    private lateinit var mMap: GoogleMap
    private lateinit var mDrawer: DrawerLayout
    private lateinit var mTBar: Toolbar
    private lateinit var mPopup: View
    private var mTBarHt: Float = 0.0f
    private var mSelMkr: Marker? = null
    private var mSelMkrIcon: BitmapDescriptor? = null
    private var mActionBarHt:Int = 0
    private var mScreenHt:Int = 0
    private var mSelRadioBtn:Int = 0

    private fun setRadioBtnOff(id: Int){
        //if (id != 0) {
        when (id) {
            R.id.icon_1hr -> setTBarMenuItemColour (id, android.R.color.holo_blue_light)
            R.id.icon_long-> setTBarMenuItemColour (id, android.R.color.holo_orange_light)
            R.id.icon_1day-> setTBarMenuItemColour (id, android.R.color.holo_green_light)
        } } //}
    private fun setRadioBtnOn (id: Int) : Boolean {
        when (id) {
            R.id.icon_1hr -> { setTBarMenuItemColour (id, android.R.color.holo_blue_dark) ;  toast("icon_1hr  !") }
            R.id.icon_long-> { setTBarMenuItemColour (id, android.R.color.holo_orange_dark); toast("icon_long !") }
            R.id.icon_1day-> { setTBarMenuItemColour (id, android.R.color.holo_green_dark) ; toast("icon_1day !") }
            else -> return false
        }
        return true
    }

    private fun setRadioBtn (id: Int) : Boolean{
        setRadioBtnOff (mSelRadioBtn)
        mSelRadioBtn = id
        return setRadioBtnOn (id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = getMapFragment()//supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mTBar = findViewById<Toolbar>(R.id.toolbar)
        mTBar.inflateMenu(R.menu.toolbar_menu)
        setTBarMenuItemColour (R.id.phone_icon   , android.R.color.black)
        setTBarMenuItemColour (R.id.email_icon   , android.R.color.holo_blue_dark)
        setTBarMenuItemColour (R.id.subscribe_icon,android.R.color.holo_red_dark)
        setRadioBtnOff (R.id.icon_1hr )
        setRadioBtnOff (R.id.icon_long)
        setRadioBtnOff (R.id.icon_1day)


        mTBar.setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.phone_icon     -> toast("phoning!")
                    R.id.email_icon     -> toast("emailing!")
                    R.id.subscribe_icon -> toast("subscribing!")
                    else -> return setRadioBtn(item.itemId)
                }
                return true
            }
        })
//        setSupportActionBar(toolbar)

        mDrawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)
//        val toggle = ActionBarDrawerToggle(
//            this,
//            drawer,
//            toolbar,
//            R.string.nav_drawer_open,
//            R.string.nav_drawer_close
//        )
//        drawer.addDrawerListener(toggle)
//        toggle.syncState()
//        if (savedInstanceState == null) {
//            showFragment(MessageFragment())
//            navView.setCheckedItem(R.id.nav_message)
//        }
    }
    private fun setTBarMenuItemColour(id: Int, colour: Int) {
        val menu = mTBar.menu
        menu.findItem(id)?.icon?.let {
            DrawableCompat.setTint( it, ContextCompat.getColor(this, colour) )
        }
    }
    private fun getMapFragment(): SupportMapFragment {
        return supportFragmentManager.findFragmentById(R.id.mapfragment) as SupportMapFragment
    }
    private fun showFragment(frag: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, frag).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_message -> showFragment(MessageFragment())
            R.id.nav_subscribe -> showFragment(SubscribeFragment())
            R.id.nav_phone -> showFragment(PhoneFragment())
            R.id.nav_send -> toast("Sending!")
        }
        mDrawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /* Manipulates the map once available. This callback is triggered when the map is ready to add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install it inside the SupportMapFragment. This method will only be triggered once the user has installed Google Play services and returned to the app.
     */
    override fun onMapReady(gmap: GoogleMap) {

        mMap = gmap //?: return
        val clr =  ContextCompat.getColor(this, android.R.color.black)
        mSelMkrIcon = getBitmapFromVector(this, R.drawable.ic_action_name, clr)

        val westHampstead = LatLng(51.546085, -0.189796)
        val dhammaShed = LatLng(51.544647, -0.065448)
        with(mMap) {
            addMarker(MarkerOptions().position(westHampstead).title("West Hampstead"))
            addMarker(
                MarkerOptions()
                    .position(dhammaShed)
                    .title("Dhamma Shed")
                  //  .icon(mSelMarkerIcon)
                    .snippet(   "• Every Evening: (just turn up)   7 –  8 pm\n" +
                                "• 3 hours: every Sunday:    10 am –  1 pm\n" +
                                "   and every Thursday:      9 am – 12 noon\n" +
                                "• One Day Course:(book by email) \n" +
                                "   last Saturday of each month: 9am–6pm\n" +
                                " We Never Cancel!  Cushions provided.\n" +
                                "        Colin Barnett                07960 130 587\n" +
                                "                  vipassana.hackney@gmail.com\n" +
                                "Full info here    https://www.dipa.dhamma.org/os/sitting/find-a-group-sit/london-hackney-group-sits/\n" +
                                "Use the side door marked \"The Dhamma Shed\".    The entry code is CX04826\n" +
                                "Details from Google Maps\n" +
                                "rear of, 8 Elrington Rd, Hackney, London E8 3BJ\n" +
                                "+44 7960 130587\n" +
                                "www.dipa.dhamma.org"
                    )
            )
             moveCamera(CameraUpdateFactory.newLatLngZoom(dhammaShed, 10f))
        }
        mTBar.visibility = View.INVISIBLE
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapClickListener(this)
        mMap.setOnInfoWindowClickListener(this)
        mMap.setInfoWindowAdapter(this)
        mMap.uiSettings.isMapToolbarEnabled = true
        mPopup = layoutInflater.inflate(R.layout.info_window, mDrawer, false)

        try {
            // Customise the styling of the base map using a JSON object defined in a raw resource file.
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style
                )
            )
            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.")
            }
        } catch (e: NotFoundException) {
            Log.e("MapsActivityRaw", "Can't find style.", e)
        }

    }

    override fun getInfoWindow(p0: Marker?): View? {
        return null // no need to change the frame
    }

    override fun getInfoContents(mk: Marker?): View? {
        //if (mk == null) return null
        //return with () {
        (mPopup.findViewById<TextView>(R.id.title  )).apply { text = mk?.getTitle() }
        (mPopup.findViewById<TextView>(R.id.snippet)).apply { text = mk?.getSnippet() }
        return mPopup //custom view for InfoWindow
    }

    override fun onMapClick(p0: LatLng?) {
        mSelMkr?. let {
            mSelMkr = null
            it.setIcon(BitmapDescriptorFactory.defaultMarker())
            it.hideInfoWindow()
            hideToolBar()
        }
    }

    override fun onMarkerClick(mkr: Marker): Boolean {
//        val changedVis = (tBar.visibility == View.INVISIBLE)
//        val changedMkr = (mkr != mLastMk)
//        if (!changedVis && !changedMkr){
//            mkr.showInfoWindow()
//            return true
//        }
//        if (changedVis || changedMkr) {
//            if (!changedVis) {  // so: changedMkr is true
//                mLastMk?.setIcon(BitmapDescriptorFactory.defaultMarker())
//                mkr.hideInfoWindow()
//            }
        if (mSelMkr != mkr){
            mSelMkr?.setIcon(BitmapDescriptorFactory.defaultMarker())
            mSelMkr = mkr
            mkr.setIcon(mSelMkrIcon)
            showToolBar(mkr)
        }
        return false  // show infoWindow and the gmap-toolbar: [ DirectionsBtn | GMapsBtn ]
    }

    private fun hideToolBar() {
        mTBar.animate()
            .translationY(mTBarHt)
            .alpha(0.0f)
            .setDuration(900)
            .setInterpolator(BounceInterpolator())
//            .setListener(object : AnimatorListenerAdapter() {
//                override fun onAnimationEnd(animation: Animator) {
//                    super.onAnimationEnd(animation)
//                    mTBar.visibility = View.INVISIBLE
//                }
//            })
    }

    private fun measuredHeight(view: View?): Int {
        view?.measure( WindowManager.LayoutParams.MATCH_PARENT
                     , WindowManager.LayoutParams.WRAP_CONTENT
        )
        return view?.measuredHeight ?: 0  // pixels
    }
    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }
    private fun showToolBar(mk: Marker) {
        if (mTBarHt == 0.0f) {
            mTBarHt = measuredHeight(mTBar).toFloat()
            mTBar.translationY = mTBarHt
            mTBar.visibility = View.VISIBLE
            mActionBarHt = actionBarHt()
            mScreenHt = getScreenHeight()
        }
       //
        mTBar.title = mk.title + ':'
        mTBar.animate()
            .translationY(0f)// linLt.height.toFloat())
            .alpha(0.75f)
            .setDuration(900)
            .setInterpolator(BounceInterpolator())
 //           .setListener(null)
            .withStartAction { // adjust the map just in cases where the selected marker is under the toolbar
                //val display = windowManager.defaultDisplay
                val proj = mMap.projection
                val mkXY = proj.toScreenLocation(mk.position)
                log("measuredHeight(mPopup) = ${measuredHeight(mPopup)}  mPopup.height = ${mPopup.height}  " )
                val target= Point(mkXY.x, mkXY.y - maxOf(0, (mPopup.height - (mScreenHt/2)) + (mActionBarHt*3/2)))
                log(" mkXY  =  $ mkXY   target = $target")
                val ptLatLng = proj.fromScreenLocation(target)
                mMap.animateCamera(CameraUpdateFactory.newLatLng(ptLatLng))
            }
    }
    private fun actionBarHt() :Int {
        val tv = TypedValue()
        return (getActivity(this)
            ?. theme
            ?. resolveAttribute(R.attr.actionBarSize, tv, true))
            ?. let { TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
                } ?: 0
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onInfoWindowClick(marker: Marker) { toast("you clicked !!!!!");    log("infoWindow")    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun toast(msg: String) {
        val t = Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT)
        t.setGravity(Gravity.BOTTOM or Gravity.END, 50, 350)
        t.view.background.setTint(ContextCompat.getColor(applicationContext, android.R.color.holo_green_dark))
        val text: TextView = t.view.findViewById(android.R.id.message)
        val tcolour = ContextCompat.getColor(applicationContext, android.R.color.background_light)
        text.setTextColor(tcolour)
        t.show()
    }

    fun gmapAction() {
        val intent = Intent(
            Intent.ACTION_VIEW,
            // see https://developers.google.com/maps/documentation/urls/guide
            Uri.parse("https://www.google.com/maps/search/?api=1&query=47.5951518,-122.3316393")
        )
        startActivity(intent)
    }

}
//    override fun onInfoWindowClick(p0: Marker?) {
//        TODO("Not yet implemented")
//    }
//    fun showGSDetails(view: View){
//
//    }
//
//internal class MyBounceInterpolator : Interpolator {
//    var mAmplitude = 1.0
//    var mFrequency = 10.0
//
//    constructor() {}
//    constructor(amp: Double, freq: Double) {
//        mAmplitude = amp
//        mFrequency = freq
//    }
//
//    override fun getInterpolation(time: Float): Float {
//        return (-1 * Math.pow(Math.E, -time / mAmplitude) * Math.cos(
//            mFrequency * time
//        ) + 1).toFloat()
//    }
//}
//class BounceInterpolator1 {
//    constructor() {}
//    //constructor(context: Context?, attrs: AttributeSet?) {}
//
//    fun getInterpolation(t: Float): Float {
//        // _b(t) = t * t * 8
//        // bs(t) = _b(t) for t < 0.3535
//        // bs(t) = _b(t - 0.54719) + 0.7 for t < 0.7408
//        // bs(t) = _b(t - 0.8526) + 0.9 for t < 0.9644
//        // bs(t) = _b(t - 1.0435) + 0.95 for t <= 1.0
//        // b(t) = bs(t * 1.1226)
//        var t = t
//        t *= 1.1226f
//        return if (t < 0.3535f) BounceInterpolator.Companion.bounce(t) else if (t < 0.7408f) BounceInterpolator.Companion.bounce(
//            t - 0.54719f
//        ) + 0.7f else if (t < 0.9644f) BounceInterpolator.Companion.bounce(t - 0.8526f) + 0.9f else BounceInterpolator.Companion.bounce(
//            t - 1.0435f
//        ) + 0.95f
//    }
//
//    /** @hide
//     */
//    fun createNativeInterpolator(): Long {
//        return NativeInterpolatorFactoryHelper.createBounceInterpolator()
//    }
//
//    companion object {
//        private fun bounce(t: Float): Float {
//            return t * t * 8.0f
//        }
//    }
//}

//class BounceInterpolator2 : Interpolator {
//    constructor() {}
//   // constructor(context: Context?, attrs: AttributeSet?) {}
//
//    override fun getInterpolation(t: Float): Float {
//        // _b(t) = t * t * 8
//        // bs(t) = _b(t) for t < 0.3535
//        // bs(t) = _b(t - 0.54719) + 0.7 for t < 0.7408
//        // bs(t) = _b(t - 0.8526) + 0.9 for t < 0.9644
//        // bs(t) = _b(t - 1.0435) + 0.95 for t <= 1.0
//        // b(t) = bs(t * 1.1226)
//        var t = t
//        t *= 1.1226f
//        return if (t < 0.3535f) bounce(t) else if (t < 0.7408f) bounce(
//            t - 0.54719f
//        ) + 0.7f else if (t < 0.9644f) bounce(t - 0.8526f) + 0.9f else bounce(
//            t - 1.0435f
//        ) + 0.95f
//    }
//
//    private fun bounce(t: Float): Float {
//        return t * t * 3.0f
//    }
//
//}