package io.tohure.changefragmentstest

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG_ONE = "first"
private const val TAG_SECOND = "second"
private const val TAG_THIRD = "third"
private const val TAG_FOURTH = "fourth"
private const val MAX_HISTORIC = 5

class MainActivity : AppCompatActivity() {

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
                currentMenuItemId = menuItem.itemId

                when (currentMenuItemId) {
                    R.id.navigation_shop -> changeFragment(TAG_ONE, FirstFragment.newInstance())
                    R.id.navigation_gifts -> changeFragment(TAG_SECOND, SecondFragment.newInstance())
                    R.id.navigation_cart -> changeFragment(TAG_THIRD, ThirdFragment.newInstance())
                    R.id.navigation_profile -> changeFragment(TAG_FOURTH, FourthFragment.newInstance())
                }

                return@setOnNavigationItemSelectedListener true
            }

            false
        }
    }

    private fun changeFragment(tagToChange: String, fragment: Fragment) {
        if (currentTag != tagToChange) {
            val ft = supportFragmentManager.beginTransaction()
            val currentFragment = supportFragmentManager.findFragmentByTag(currentTag)
            val fragmentToReplaceByTag = supportFragmentManager.findFragmentByTag(tagToChange)

            oldTag = currentTag
            currentTag = tagToChange

            if (fragmentToReplaceByTag != null) {
                currentFragment?.let { ft.hide(it).show(fragmentToReplaceByTag) }
            } else {
                currentFragment?.let { ft.hide(it).add(R.id.frame_container, fragment, tagToChange) }
            }

            ft.commit()

            addBackStack()
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

        val ft = supportFragmentManager.beginTransaction()

        val currentFragmentByTag = supportFragmentManager.findFragmentByTag(lastState.currentFragmentTag)
        val oldFragmentByTag = supportFragmentManager.findFragmentByTag(lastState.oldFragmentTag)

        if ((currentFragmentByTag != null && currentFragmentByTag.isVisible) &&
                (oldFragmentByTag != null && oldFragmentByTag.isHidden)) {
            ft.hide(currentFragmentByTag).show(oldFragmentByTag)
        }

        ft.commit()

        val menu = navigation.menu

        when (lastState.oldFragmentTag) {
            TAG_ONE -> setMenuItem(menu.getItem(0))
            TAG_SECOND -> setMenuItem(menu.getItem(1))
            TAG_THIRD -> setMenuItem(menu.getItem(2))
            TAG_FOURTH -> setMenuItem(menu.getItem(3))
        }

        //Remove from Stack
        listState.removeLast()

        if (listState.isEmpty()) {
            currentTag = TAG_ONE
            oldTag = TAG_ONE
        } else {
            currentTag = listState.last().currentFragmentTag
            oldTag = listState.last().oldFragmentTag
        }
        updateLog()
    }

    private fun updateLog() {
        tvCurrentTag.text = currentTag
        tvOldTag.text = oldTag
    }

    private fun setMenuItem(menuItem: MenuItem) {
        menuItem.isChecked = true
        currentMenuItemId = menuItem.itemId
    }

    private fun loadFirstFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(
                R.id.frame_container,
                FirstFragment.newInstance(),
                TAG_ONE)
        transaction.commit()
    }

    //Like YouTube
    private fun addBackStack() {
        updateLog()

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
