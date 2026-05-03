package com.ggg.kt.wanandroidbyyohen.ui.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ggg.kt.wanandroidbyyohen.R
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.databinding.FragmentMineBinding
import kotlinx.coroutines.launch

class MineFragment : Fragment(R.layout.fragment_mine) {
    private var _binding: FragmentMineBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MineViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClick()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUserInfo()
    }

    private fun initClick() {
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.login_fragment)
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }

        binding.btnCollect.setOnClickListener {
            findNavController().navigate(R.id.collect_article_fragment)
        }
    }

    private fun observeData() {
        observeUserInfo()
        observeLogout()
    }

    private fun observeUserInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userInfoState.collect { state ->
                    when (state) {
                        null -> Unit

                        is UiState.Loading -> {
                            binding.tvState.visibility = View.VISIBLE
                            binding.tvState.text = "加载中..."
                        }

                        is UiState.Success -> {
                            binding.tvState.visibility = View.GONE

                            val userInfo = state.data.userInfoData.userInfo
                            val coinInfo = state.data.userInfoData.coinInfo

                            binding.tvUsername.text = userInfo?.username ?: "已登录"

                            val sourceText = if (state.data.isFromLocal) {
                                "本地缓存"
                            } else {
                                "已同步"
                            }

                            binding.tvUserInfo.text =
                                "积分：${coinInfo?.coinCount ?: 0}  等级：${coinInfo?.level ?: 0}  排名：${coinInfo?.rank ?: "-"}  $sourceText"

                            binding.btnLogin.visibility = View.GONE
                            binding.btnLogout.visibility = View.VISIBLE
                            binding.btnCollect.visibility = View.VISIBLE
                        }

                        is UiState.Error -> {
                            binding.tvState.visibility = View.GONE
                            binding.tvUsername.text = "未登录"
                            binding.tvUserInfo.text = "登录后查看积分和收藏"
                            binding.btnLogin.visibility = View.VISIBLE
                            binding.btnLogout.visibility = View.GONE
                            binding.btnCollect.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun observeLogout() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.logoutState.collect { state ->
                    when (state) {
                        null -> Unit

                        is UiState.Loading -> {
                            binding.tvState.visibility = View.VISIBLE
                            binding.tvState.text = "退出中..."
                        }

                        is UiState.Success -> {
                            binding.tvState.visibility = View.GONE

                            binding.tvUsername.text = "未登录"
                            binding.tvUserInfo.text = "登录后查看积分和收藏"

                            binding.btnLogin.visibility = View.VISIBLE
                            binding.btnLogout.visibility = View.GONE
                            binding.btnCollect.visibility = View.GONE
                        }

                        is UiState.Error -> {
                            binding.tvState.visibility = View.VISIBLE
                            binding.tvState.text = state.message
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}