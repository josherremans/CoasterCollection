package josh.android.coastercollection.adapters;

import android.app.Activity;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import josh.android.coastercollection.R;
import josh.android.coastercollection.activities.AddSeriesActivity;
import josh.android.coastercollection.activities.GalleryActivity;
import josh.android.coastercollection.bo.Series;
import josh.android.coastercollection.bo.TrademarkSeriesGroup;
import josh.android.coastercollection.enums.IIntentExtras;

/**
 * Created by Jos on 20/03/2017.
 */

public class SeriesExpandableListAdapter extends BaseExpandableListAdapter {

    private final SparseArray<TrademarkSeriesGroup> groups;

    public LayoutInflater inflater;
    public Activity activity;

    public SeriesExpandableListAdapter(Activity act, SparseArray<TrademarkSeriesGroup> groups) {
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).series.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Series children = (Series) getChild(groupPosition, childPosition);
        TextView txtVw = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_series_list_details, null);
        }

        String str = children.getSeries();

        int nStr = str.length();

        if (children.getMaxNumber() > 0) {
            str = str + " (#" + children.getMaxNumber() + (children.isOrdered() ? "s" : "") + ")";
        }

        txtVw = (TextView) convertView.findViewById(R.id.txtExpListSeries);

        SpannableString text = new SpannableString(str);

        text.setSpan(new TextAppearanceSpan(null, 0, 60, null, null), 0, nStr, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (str.length() > nStr) {
            text.setSpan(new TextAppearanceSpan(null, 0, 40, null, null), nStr + 1, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        txtVw.setText(text, TextView.BufferType.SPANNABLE);

//        txtVw.setText(str);

//        final String fstr = str;

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maxOrdered = "";

                if (children.getMaxNumber() > 0) {
                    maxOrdered += children.getMaxNumber();

                    if (children.isOrdered()) {
                        maxOrdered += "s";
                    }
                }

                Intent galleryIntent = new Intent(activity, GalleryActivity.class);

                galleryIntent.putExtra(IIntentExtras.EXTRA_SERIESID, children.getSeriesID());
                galleryIntent.putExtra(IIntentExtras.EXTRA_SERIESMAX_ORDERED, maxOrdered);
                galleryIntent.putExtra(IIntentExtras.EXTRA_GALLERY_SUBTITLE, children.getSeries());

                activity.startActivity(galleryIntent);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent alterSeriesIntent = new Intent(activity, AddSeriesActivity.class);

                alterSeriesIntent.putExtra(IIntentExtras.EXTRA_SERIESID, children.getSeriesID());

                activity.startActivity(alterSeriesIntent);

                return false;
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).series.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_series_list_group, null);
        }

        TextView txtGroup = (TextView) convertView.findViewById(R.id.txtGroup);

        TrademarkSeriesGroup group = (TrademarkSeriesGroup) getGroup(groupPosition);

        txtGroup.setText(group.trademark);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
