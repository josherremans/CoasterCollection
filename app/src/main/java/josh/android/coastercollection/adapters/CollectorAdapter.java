package josh.android.coastercollection.adapters;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import josh.android.coastercollection.R;
import josh.android.coastercollection.bo.Collector;

/**
 * Created by Jos on 2/02/2017.
 */
public class CollectorAdapter extends BaseAdapter {

    private ArrayList<Collector> lstCollectors;

    private LayoutInflater layoutInflater;

    private int listViewTypeId;
    private boolean showAllInfo = false;

    private Context cx;

    public CollectorAdapter(Context context, String listViewType, ArrayList<Collector> lstCollectors) {
        cx = context;

        if (listViewType.equals(cx.getResources().getStringArray(R.array.pref_listview_type_values)[0])) { // "CardType"
            listViewTypeId = R.layout.item_collector_list;
            showAllInfo = true;
        }

        if (listViewType.equals(cx.getResources().getStringArray(R.array.pref_listview_type_values)[1])) { // "FullWidthType"
            listViewTypeId = R.layout.item_collector_list;
            showAllInfo = true;
        }

        if (listViewType.equals(cx.getResources().getStringArray(R.array.pref_listview_type_values)[2])) { // "FullWidthTypeSum"
            listViewTypeId = R.layout.item_collector_list;
            showAllInfo = false;
        }

        this.lstCollectors = lstCollectors;

        layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (lstCollectors == null) {
            return 0;
        } else {
            return lstCollectors.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return lstCollectors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return lstCollectors.get(position).getCollectorID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        Collector collector = (Collector) getItem(position);

        // reuse views
        if (rowView == null) {
            rowView = layoutInflater.inflate(listViewTypeId, null);

            // configure view holder
            ViewHolder viewHolder = new ViewHolder(rowView);

            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.collectorFullName.setText(collector.getFullName());

        if ((collector.getInitials() != null) && (collector.getInitials().length() > 0)) {
            holder.collectorInitials.setText("(" + collector.getInitials() + ")");
        }

        if ((collector.getAlias() != null) && (collector.getAlias().length() > 0)) {
            holder.collectorAlias.setText(collector.getAlias());
        }

        return rowView;
    }

    private class ViewHolder {
        public TextView collectorFullName;
        public TextView collectorInitials;
        public TextView collectorAlias;

        public ViewHolder(View parent) {
            collectorFullName = (TextView) parent.findViewById(R.id.txtCollectorFullName);
            collectorInitials = (TextView) parent.findViewById(R.id.txtCollectorInitials);
            collectorAlias = (TextView) parent.findViewById(R.id.txtCollectorAlias);
        }
    }
}
