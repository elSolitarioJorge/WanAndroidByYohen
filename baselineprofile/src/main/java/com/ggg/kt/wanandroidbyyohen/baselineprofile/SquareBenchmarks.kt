package com.ggg.kt.wanandroidbyyohen.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SquareBenchmarks {
    @get:Rule
    val rule = MacrobenchmarkRule()
    private val targetPackage =
        InstrumentationRegistry.getArguments().getString("targetAppId")
            ?: error("targetAppId not passed as instrumentation runner arg")
    private val device =
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    @Test
    fun openSquare() {
        rule.measureRepeated(
            packageName = targetPackage,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = CompilationMode.Partial(BaselineProfileMode.Require),
            iterations = 10,
            setupBlock = {
                // 每轮都从相同状态开始，避免上一次测试影响结果
                killProcess()
                pressHome()
                startActivityAndWait()
                check(
                    device.wait(
                        Until.hasObject(
                            By.res(targetPackage, "bottom_nav")
                        ),
                        10_000
                    )
                ) {
                    "首页启动失败：没有找到 bottom_nav"
                }
                check(
                    device.wait(
                        Until.hasObject(
                            By.res(targetPackage, "rv_articles")
                        ),
                        10_000
                    )
                ) {
                    "首页加载失败：没有找到 rv_articles"
                }
                device.waitForIdle()
            },
            measureBlock = {
                val squareTab = device.wait(
                    Until.findObject(
                        By.res(targetPackage, "square_fragment")
                    ),
                    10_000
                )
                checkNotNull(squareTab) {
                    "没有找广场底部导航按钮"
                }
                squareTab.click()
                check(
                    device.wait(
                        Until.hasObject(
                            By.res(targetPackage, "tv_square_title")
                        ),
                        10_000
                    )
                ) {
                    "广场页面打开失败：没有找到标题"
                }
                check(
                    device.wait(
                        Until.hasObject(
                            By.res(targetPackage, "rv_square_articles")
                        ),
                        10_000
                    )
                ) {
                    "广场列表创建失败"
                }
                device.waitForIdle()
            }

        )
    }
}