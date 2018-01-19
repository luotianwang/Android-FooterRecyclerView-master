package recyclerview.demo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import adapter.MainAdapter;
import footer.android.FooterRecyclerView;

/**
 * Created by my on 2016/11/24.
 */

public class  MainUI extends AppCompatActivity {
    private FooterRecyclerView footer_rv;
    private MainAdapter m_adapter;
    int i;
    private List<String> list=new ArrayList<String>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.main_ui);
        setData();
       initView();
    }

    private void setData() {
        for (int i=0;i<8;i++){
            list.add(""+i);
        }
    }
public  void addData(){
    i++;
    for (int i=0;i<3;i++){
        list.add(""+i);
    }
    m_adapter.setList(list);
    m_adapter.notifyDataSetChanged();
    footer_rv.onLoadMoreComplete();
}
    private void initView() {
        m_adapter=new MainAdapter(this,list);
        footer_rv=(FooterRecyclerView)findViewById(R.id.footer_rv);
        footer_rv.setLayoutManager(new LinearLayoutManager(this));
        //设置滑到底部自动加载
//        footer_rv.setmIsAutoLoadMore(true);
        //设置监听默认开启上拉加载
        footer_rv.setOnLoadListener(new FooterRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                        if (i<2){
                            addData();
                        }else {
                            footer_rv.closeFooter("大哥，没有更多数据了");
                        }
//                    }
//                },2000);
            }
        });
        footer_rv.setAdapter(m_adapter);
    }
}
