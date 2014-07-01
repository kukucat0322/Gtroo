package cn.burgeon.core.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.burgeon.core.R;
import cn.burgeon.core.bean.Product;

/**
 * Created by Simon on 2014/4/16.
 */
public class CheckScanLVAdapter extends BaseAdapter {

    private List<Product> list;
    private Context context;

    public CheckScanLVAdapter(List<Product> list, Context context) {
        super();
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.check_scan_item, null);
            holder.textView1 = (TextView) convertView.findViewById(R.id.check_shelf);
            holder.textView2 = (TextView) convertView.findViewById(R.id.check_barcode);
            holder.textView3 = (TextView) convertView.findViewById(R.id.check_count);
            holder.textView4 = (TextView) convertView.findViewById(R.id.check_color);
            holder.textView5 = (TextView) convertView.findViewById(R.id.check_size);
            holder.textView6 = (TextView) convertView.findViewById(R.id.check_kuanhao);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = list.get(position);
        holder.textView1.setText(product.getShelf());
        holder.textView2.setText(product.getBarCode());
        holder.textView3.setText(product.getCount());
        holder.textView4.setText(product.getColor());
        holder.textView5.setText(product.getSize());
        holder.textView6.setText(product.getName());

        return convertView;
    }

    class ViewHolder {
        TextView textView1;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        TextView textView5;
        TextView textView6;
    }
}
