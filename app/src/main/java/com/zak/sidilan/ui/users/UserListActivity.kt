package com.zak.sidilan.ui.users

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.zak.sidilan.R
import com.zak.sidilan.databinding.ActivityUserListBinding
import com.zak.sidilan.ui.bookdetail.BookDetailActivity
import com.zak.sidilan.ui.books.BooksAdapter
import com.zak.sidilan.util.FirstItemMarginDecoration
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

val userListActivityModule = module {
    factory { UserListActivity() }
}
class UserListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserListBinding
    private val viewModel: UserListViewModel by viewModel()

    private lateinit var adapter: UsersAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)

        setupView()
        setupRecyclerView()
        setupViewModel()
        setContentView(binding.root)
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Manajemen Pengguna"
    }
    private fun setupRecyclerView() {
        adapter = UsersAdapter(this) { user ->
            val intent = Intent(this, UserDetailActivity::class.java)
            intent.putExtra("userId", user.id)
            startActivity(intent)
        }
        val pixel = resources.getDimensionPixelOffset(R.dimen.first_item_margin)
        val decorator = FirstItemMarginDecoration(pixel)
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = adapter
        binding.rvUsers.itemAnimator = DefaultItemAnimator()
        binding.rvUsers.addItemDecoration(decorator)
    }

    private fun setupViewModel() {
        viewModel.getUsers()
        viewModel.userList.observe(this) { books ->
            adapter.submitList(books)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}