package io.tohure.changefragmentstest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.app.Fragment
import android.util.Log

class MainActivity : AppCompatActivity() {

    private var currentFragment: Fragment? = null

    private val listState = mutableListOf<StateFragment>()
    private var currentTag: String = "one"
    private var oldTag: String = "one"
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
                        currentTag = "one"
                        fragment = FirstFragment.newInstance("one", "first")
                        loadFragment(fragment, currentTag)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.navigation_gifts -> {
                        currentTag = "second"
                        fragment = SecondFragment.newInstance("two", "second")
                        loadFragment(fragment, currentTag)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.navigation_cart -> {
                        currentTag = "third"
                        fragment = ThirdFragment.newInstance("three", "third")
                        loadFragment(fragment, currentTag)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.navigation_profile -> {
                        currentTag = "fourth"
                        fragment = FourthFragment.newInstance("four", "fourth")
                        loadFragment(fragment, currentTag)
                        return@setOnNavigationItemSelectedListener true
                    }
                }

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
            "one" -> menu.getItem(0).isChecked = true
            "second" -> menu.getItem(1).isChecked = true
            "third" -> menu.getItem(2).isChecked = true
            "fourth" -> menu.getItem(3).isChecked = true
        }

    }

    private fun loadFirstFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        currentFragment = FirstFragment.newInstance("one", "first")
        transaction.add(R.id.frame_container, currentFragment, "one")
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

    private fun addBackStack() {
        Log.d("thr add", "$currentTag - $oldTag")

        when (listState.size) {
            5 -> {
                listState[1].oldFragmentTag = "one"
                listState[0] = listState[1]
                listState[1] = listState[2]
                listState[2] = listState[3]
                listState[3] = listState[4]
                listState[4] = StateFragment(currentTag, oldTag)
            }
            else -> {
                listState.add(StateFragment(currentTag, oldTag))
            }
        }

    }

}