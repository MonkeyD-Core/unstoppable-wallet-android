package io.horizontalsystems.bankwallet.modules.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.BaseFragment
import io.horizontalsystems.bankwallet.modules.info.ui.InfoBody
import io.horizontalsystems.bankwallet.modules.info.ui.InfoHeader
import io.horizontalsystems.bankwallet.modules.info.ui.InfoSubHeader
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.TranslatableString
import io.horizontalsystems.bankwallet.ui.compose.components.AppBar
import io.horizontalsystems.bankwallet.ui.compose.components.MenuItem
import io.horizontalsystems.core.findNavController

class TokenTvlInfoFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
            )

            setContent {
                ComposeAppTheme {
                    InfoScreen(
                        findNavController()
                    )
                }
            }
        }
    }

}

@Composable
private fun InfoScreen(
    navController: NavController
) {

    Surface(color = ComposeAppTheme.colors.tyler) {
        Column {
            AppBar(
                menuItems = listOf(
                    MenuItem(
                        title = TranslatableString.ResString(R.string.Button_Close),
                        icon = R.drawable.ic_close,
                        onClick = { navController.popBackStack() }
                    )
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                InfoHeader(R.string.CoinPage_TokenTvl)
                InfoBody(R.string.CoinPage_TokenTvl_Description)
                InfoSubHeader(R.string.CoinPage_TvlRank)
                InfoBody(R.string.CoinPage_TvlRank_Description)
                InfoSubHeader(R.string.CoinPage_TvlMCapRatio)
                InfoBody(R.string.CoinPage_TvlMCapRatio_Description)
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
