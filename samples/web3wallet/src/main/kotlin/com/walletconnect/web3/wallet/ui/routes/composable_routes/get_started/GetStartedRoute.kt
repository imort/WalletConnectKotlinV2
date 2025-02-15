package com.walletconnect.web3.wallet.ui.routes.composable_routes.get_started

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.walletconnect.web3.wallet.sample.R
import com.walletconnect.web3.wallet.ui.common.findActivity
import com.walletconnect.web3.wallet.ui.common.generated.GetStarted
import com.walletconnect.web3.wallet.ui.common.generated.Welcome
import com.walletconnect.web3.wallet.ui.routes.Route
import timber.log.Timber

@Composable
fun GetStartedRoute(navController: NavController) {
    Image(
        modifier = Modifier.fillMaxSize(),
        painter = painterResource(id = if (isSystemInDarkTheme()) R.drawable.black_background else R.drawable.white_background),
        contentDescription = "Background"
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Welcome()
        }
        val context = LocalContext.current
        val activity = context.findActivity()
        val shape = RoundedCornerShape(20.dp)
        Column() {
            GetStarted(
                Modifier
                    .padding(horizontal = 20.dp)
                    .shadow(elevation = 4.dp, shape = shape)
                    .clip(shape)
                    .clickable {
                        saveGetStartedVisited(activity)
                        navController.navigate(Route.Connections.path)
                    }
            )
            Spacer(modifier = Modifier.height(54.dp))
        }
    }
}

fun saveGetStartedVisited(activity: Activity?) {
    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: run {
        Timber.tag("Web3Wallet").e("No default Shared Preferences")
        return
    }

    with(sharedPref.edit()) {
        putBoolean("get_started_visited", true)
        apply()
    }
}

