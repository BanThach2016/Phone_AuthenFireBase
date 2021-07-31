package com.anhtong8x.phone_authenfirebase.realtimedb;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anhtong8x.phone_authenfirebase.R;

import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {
    Context _context;
    ArrayList<UserModel> _arrUser;
    LayoutInflater _inflater;

    public UserAdapter(Context context, ArrayList<UserModel> arrUser) {
        this._context = context;
        this._arrUser = arrUser;
        _inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return _arrUser.size();
    }

    @Override
    public Object getItem(int position) {
        return _arrUser.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = _inflater.inflate(R.layout.item_lst_user, null);

        UserModel u = _arrUser.get(position);

        TextView t1 = v.findViewById(R.id.txtIdUser);
        TextView t2 = v.findViewById(R.id.txtNameUser);

        t1.setText(u.getIdUser());
        t2.setText(u.getName());

        return v;
    }
}
