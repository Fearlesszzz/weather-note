package com.example.rain.ui.record;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.rain.R;
import com.example.rain.db.DBUtils;
import com.example.rain.db.SQLiteHelper;
import com.example.rain.ui.dashboard.DashboardViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RecordFragment extends Fragment {
    ImageView note_back;
    EditText content;
    ImageButton delete;
    ImageButton note_save;
    SQLiteHelper mSQLiteHelper;
    TextView noteName;
    BottomNavigationView nav_view;
    String id;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_record, container, false);
        note_back = root.findViewById(R.id.note_back);
        content = root.findViewById(R.id.note_content);
        delete = root.findViewById(R.id.delete);
        note_save = root.findViewById(R.id.note_save);
        noteName = root.findViewById(R.id.note_name);
        nav_view = requireActivity().findViewById(R.id.nav_view);
        nav_view.setVisibility(View.GONE);
        initData();
//        dashboardViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//
//            }
//        });
        return root;
    }
    protected void initData() {
        mSQLiteHelper = new SQLiteHelper(requireActivity());
        Bundle bundle = getArguments();
        if (bundle!=null){
            id = bundle.getString("id");
            if (id != null){
                System.out.println(bundle.getString("content"));
                content.setText(bundle.getString("content"));
            }
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        note_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_recordFragment_to_navigation_dashboard);
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                nav_view.setVisibility(View.VISIBLE);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.setText("");
            }
        });
        note_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteContent=content.getText().toString().trim();
                if (id != null){//修改操作
                    if (noteContent.length()>0){
                        if (mSQLiteHelper.updateData(id, noteContent, DBUtils.getTime())){
                            showToast("修改成功");
                        }else {
                            showToast("修改失败");
                        }
                    }else {
                        showToast("修改内容不能为空!");
                    }
                }else {
                    //向数据库中添加数据
                    if (noteContent.length()>0){
                        if (mSQLiteHelper.insertData(noteContent, DBUtils.getTime())){
                            showToast("保存成功");
                        }else {
                            showToast("保存失败");
                        }
                    }else {
                        showToast("修改内容不能为空!");
                    }
                }
            }
        });
    }
    public void showToast(String message){
        Toast.makeText(requireActivity(),message, Toast.LENGTH_SHORT).show();
    }
}
