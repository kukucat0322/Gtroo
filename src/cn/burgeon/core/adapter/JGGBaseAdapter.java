package cn.burgeon.core.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.burgeon.core.R;

/**
 * Created by Simon on 2014/4/17.
 */
public class JGGBaseAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mTextVals;
    private int[] mImgVals;
    private int mLayoutRes;

    public JGGBaseAdapter(Context c, String[] textVals, int[] imgVals, int layoutRes) {
        this.mContext = c;
        this.mTextVals = textVals;
        this.mImgVals = imgVals;
        this.mLayoutRes = layoutRes;
    }

    @Override
    public int getCount() {
        return mTextVals.length;
    }

    @Override
    public Object getItem(int position) {
        return mTextVals[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mLayoutRes, null);
            holder = new ViewHolder();
            holder.gridIV = (ImageView) convertView.findViewById(R.id.gridIV);
            holder.gridTV = (TextView) convertView.findViewById(R.id.gridTV);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //赋值
        holder.gridIV.setBackgroundResource(mImgVals[position]);
        holder.gridTV.setText(mTextVals[position]);
        return convertView;
    }

    static class ViewHolder {
        ImageView gridIV;
        TextView gridTV;
    }
}
