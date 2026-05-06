package com.ggg.kt.wanandroidbyyohen.ui.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ggg.kt.wanandroidbyyohen.R
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.common.extension.applyTopBarInsets
import com.ggg.kt.wanandroidbyyohen.data.model.MineUiState
import com.ggg.kt.wanandroidbyyohen.data.model.UserInfoData
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

        initInsets()
        initClick()
        observeData()
    }

    private fun initInsets() {
        binding.topContainer.applyTopBarInsets()
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

        binding.btnMyShare.setOnClickListener {
            findNavController().navigate(R.id.my_share_fragment)
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
                        is MineUiState.Loading -> Unit

                        is MineUiState.LoggedOut -> {
                            updateLoginState(false)
                        }

                        is MineUiState.Content -> {
                            updateLoginState(true, state.userInfoData)
                        }

                        is MineUiState.Error -> {
                            updateLoginState(false)
                            Toast.makeText(
                                requireContext(),
                                state.message.ifBlank { "网络异常，请检查网络连接后重试" },
                                Toast.LENGTH_SHORT
                            ).show()
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

                        is UiState.Loading -> Unit

                        is UiState.Success -> {
                            updateLoginState(false)
                        }

                        is UiState.Error -> Unit
                    }
                }
            }
        }
    }

    private fun updateLoginState(isLoggedIn: Boolean, user: UserInfoData? = null) {
        if (isLoggedIn && user != null) {
            val userInfo = user.userInfo
            val coinInfo = user.coinInfo
            val displayName = userInfo?.nickname
                ?.takeIf { it.isNotBlank() }
                ?: userInfo?.username?.takeIf { it.isNotBlank() }
                ?: "已登录用户"
            val firstChar = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
            binding.tvAvatar.text = firstChar

            binding.tvUsername.text = displayName
            binding.tvUserInfo.text = if (userInfo?.id != null && userInfo.id > 0) {
                "ID: ${userInfo.id}"
            } else {
                "登录以解锁更多功能"
            }

            binding.tvPoints.text = coinInfo?.coinCount?.toString() ?: "--"
            binding.tvLevel.text = coinInfo?.level?.let { "Lv. $it" } ?: "--"
            binding.tvRank.text = coinInfo?.rank?.takeIf { it.isNotBlank() } ?: "--"

            binding.btnLogout.visibility = View.VISIBLE
            binding.btnLogin.visibility = View.GONE
        } else {
            binding.tvAvatar.text = "?"
            binding.tvUsername.text = "未登录"
            binding.tvUserInfo.text = "点击此处登录以解锁更多功能"

            binding.tvPoints.text = "--"
            binding.tvLevel.text = "--"
            binding.tvRank.text = "--"

            binding.btnLogout.visibility = View.GONE
            binding.btnLogin.visibility = View.VISIBLE
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
