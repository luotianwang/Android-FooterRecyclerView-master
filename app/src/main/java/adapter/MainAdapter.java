package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import recyclerview.demo.R;

/**
 * Created by my on 2016/11/27.
 */

public class MainAdapter  extends RecyclerView.Adapter<MainAdapter.MainViewholder> {
   private LayoutInflater layout;
    private List<String >  list;

    public  MainAdapter(Context context,List<String> list){
        layout=LayoutInflater.from(context);
        this.list=list;
   }

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public MainViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainViewholder(layout.inflate(R.layout.main_item,parent,false));
    }

    @Override
    public void onBindViewHolder(MainViewholder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public  class  MainViewholder extends RecyclerView.ViewHolder {
    public MainViewholder(View itemView) {
        super(itemView);
    }
}
}
