package io.tohure.changefragmentstest

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG_ONE = "first"
private const val TAG_SECOND = "second"
private const val TAG_THIRD = "third"
private const val TAG_FOURTH = "fourth"
private const val MAX_HISTORIC = 5

class MainActivity : AppCompatActivity() {

    private var currentFragment: Fragment? = null

    private val listState = mutableListOf<StateFragment>()
    private var currentTag: String = TAG_ONE
    private var oldTag: String = TAG_ONE
    private var currentMenuItemId: Int = R.id.navigation_shop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) loadFirstFragment()
        init()
    }

    private fun init() {

        navigation.setOnNavigationItemSelectedListener { menuItem ->

            if (currentMenuItemId != menuItem.itemId) {

                val fragment: Fragment
                oldTag = currentTag

                currentMenuItemId = menuItem.itemId

                when (currentMenuItemId) {
                    R.id.navigation_shop -> {
                        currentTag = TAG_ONE
                        fragment = FirstFragment.newInstance()
                        loadFragment(fragment, currentTag)
                    }
                    R.id.navigation_gifts -> {
                        currentTag = TAG_SECOND
                        fragment = SecondFragment.newInstance()
                        loadFragment(fragment, currentTag)
                    }
                    R.id.navigation_cart -> {
                        currentTag = TAG_THIRD
                        fragment = ThirdFragment.newInstance()
                        loadFragment(fragment, currentTag)
                    }
                    R.id.navigation_profile -> {
                        currentTag = TAG_FOURTH
                        fragment = FourthFragment.newInstance()
                        loadFragment(fragment, currentTag)
                    }
                }

                return@setOnNavigationItemSelectedListener true

            }

            false
        }

    }

    override fun onBackPressed() {

        if (listState.size >= 1) {
            recoverFragment()
        } else {
            super.onBackPressed()
        }

    }

    private fun recoverFragment() {

        val lastState = listState.last()
        listState.removeAt(listState.size - 1)

        currentTag = lastState.currentFragmentTag
        oldTag = lastState.oldFragmentTag

        Log.d("thr recover", "$currentTag - $oldTag")

        val ft = supportFragmentManager.beginTransaction()

        val currentFragment = supportFragmentManager.findFragmentByTag(currentTag)
        val oldFragment = supportFragmentManager.findFragmentByTag(oldTag)

        if (currentFragment.isVisible && oldFragment.isHidden) {
            ft.hide(currentFragment).show(oldFragment)
        }

        ft.commit()

        val menu = navigation.menu

        when (oldTag) {
            TAG_ONE -> menu.getItem(0).isChecked = true
            TAG_SECOND -> menu.getItem(1).isChecked = true
            TAG_THIRD -> menu.getItem(2).isChecked = true
            TAG_FOURTH -> menu.getItem(3).isChecked = true
        }

    }

    private fun loadFirstFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        currentFragment = FirstFragment.newInstance()
        transaction.add(R.id.frame_container, currentFragment, TAG_ONE)
        transaction.commit()
    }

    private fun loadFragment(fragment: Fragment, tag: String) {

        if (currentFragment !== fragment) {
            val ft = supportFragmentManager.beginTransaction()

            if (fragment.isAdded) {
                ft.hide(currentFragment).show(fragment)
            } else {
                ft.hide(currentFragment).add(R.id.frame_container, fragment, tag)
            }
            currentFragment = fragment

            ft.commit()

            addBackStack()
        }

    }

    //Like YouTube
    private fun addBackStack() {
        Log.d("thr add", "$currentTag - $oldTag")

        when (listState.size) {
            MAX_HISTORIC -> {

                listState[1].oldFragmentTag = TAG_ONE
                val firstState = listState[1]

                for (i in listState.indices) {
                    if (listState.indices.contains((i + 1))) {
                        listState[i] = listState[i + 1]
                    }
                }

                listState[0] = firstState
                listState[listState.lastIndex] = StateFragment(currentTag, oldTag)
            }
            else -> {
                listState.add(StateFragment(currentTag, oldTag))
            }
        }

    }

}
