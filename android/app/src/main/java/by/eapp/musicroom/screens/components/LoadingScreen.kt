package by.eapp.musicroom.screens.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LoadingScreen() {
    val composition by rememberLottieComposition(
    )
}

@Composable
fun lottieSpec(
    animation: AnimationResource,
): LottieCompositionSpec {
    val vaSdkResourceProvider = LocalVaResourceProvider.current
    val animationRemember = remember(animation) {
        vaSdkResourceProvider.getAnimationPath(animation)
    }
    return LottieCompositionSpec.File(animationRemember)
}