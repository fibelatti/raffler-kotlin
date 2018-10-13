package com.fibelatti.raffler.core.platform

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.orZero

private const val TAG = "dialog"
var Bundle.requestCode by BundleDelegate.Int("REQUEST_CODE")

@Navigator.Name("dialog_fragment")
class DialogNavigator(
    private val fragmentManager: FragmentManager
) : Navigator<DialogNavigator.Destination>() {

    override fun navigate(destination: Destination, args: Bundle?, navOptions: NavOptions?, navigatorExtras: Extras?) {
        val fragment = destination.createFragment(args)

        fragment.setTargetFragment(fragmentManager.primaryNavigationFragment, args?.requestCode.orZero())
        fragment.show(fragmentManager, TAG)

        dispatchOnNavigatorNavigated(destination.id, BACK_STACK_UNCHANGED)
    }

    override fun createDestination(): Destination = Destination(this)

    override fun popBackStack(): Boolean = true

    class Destination(navigator: Navigator<out NavDestination>) : NavDestination(navigator) {
        private var fragmentClass: Class<out DialogFragment>? = null

        override fun onInflate(context: Context, attrs: AttributeSet) {
            super.onInflate(context, attrs)

            val a = context.resources.obtainAttributes(attrs, R.styleable.FragmentNavigator)
            a.getString(R.styleable.FragmentNavigator_android_name)?.let { className ->
                fragmentClass = parseClassFromName(context, className, DialogFragment::class.java)
            }
            a.recycle()
        }

        fun createFragment(args: Bundle?): DialogFragment {
            val fragment = fragmentClass?.newInstance()
                ?: throw IllegalStateException("fragment class not set")

            return fragment.apply { arguments = args }
        }
    }
}
