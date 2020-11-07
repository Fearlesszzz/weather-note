package com.example.rain.ui.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rain.MainActivity;
import com.example.rain.R;
import com.example.rain.bean.NotepadBean;
import com.example.rain.db.SQLiteHelper;
import com.example.rain.ui.record.RecordFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class DashboardFragment extends Fragment {
    RecyclerView recyclerView;
    List<NotepadBean> list;
    SQLiteHelper mSQLiteHelper;
    NotepadAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        FloatingActionButton add = root.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_navigation_dashboard_to_recordFragment);
            }
        });
        initData();
        return root;
    }
    // 初始化记事本数据
    protected void initData() {
        mSQLiteHelper = new SQLiteHelper(requireActivity()); //创建数据库
        showQueryData();
    }

    // 展示记事本数据
    private void showQueryData() {
        if (list != null) {
            list.clear();
        }
        //从数据库中查询数据(保存的标签)
        list = mSQLiteHelper.query();
        adapter = new NotepadAdapter(requireActivity(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);

        //  点击事件
        adapter.setOnItemClickListener(new NotepadAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NotepadBean notepadBean = list.get(position);
                Bundle bundle = new Bundle();
                String id = notepadBean.getId();
                String context = notepadBean.getNotepadContent();
                bundle.putString("id", id);
                bundle.putString("content", context);
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_navigation_dashboard_to_recordFragment, bundle);
            }
        });

        //  长按事件
        adapter.setOnItemLongClickListener(new NotepadAdapter.OnRecyclerItemLongListener() {
            @Override
            public void onItemLongClick(View view, final int position) {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                        .setMessage("是否删除此事件？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NotepadBean notepadBean = list.get(position);
                                if (mSQLiteHelper.deleteData(notepadBean.getId())) {
                                    list.remove(position);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(requireActivity(), "删除成功",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog = builder.create();
                dialog.show();

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            showQueryData();
        }
    }
}