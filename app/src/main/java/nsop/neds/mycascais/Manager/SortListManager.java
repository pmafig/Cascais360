package nsop.neds.mycascais.Manager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nsop.neds.mycascais.Entities.BlockEntity;
import nsop.neds.mycascais.Entities.DashboardEntity;
import nsop.neds.mycascais.Entities.FrameEntity;

public class SortListManager extends AsyncTask<String, Void, DashboardEntity> {

    RelativeLayout loading;
    LinearLayout mainContent;
    Context context;
    LinearLayout sorting;

    JSONObject response;

    DashboardEntity dboard;

    List<String> sortingText;
    List<String> sortingKey;

    public SortListManager(Context context, LinearLayout mainContent, RelativeLayout loading, LinearLayout sorting){
        this.loading = loading;
        this.context = context;
        this.mainContent = mainContent;
        this.sorting = sorting;
        dboard = new DashboardEntity();
        sortingText = new ArrayList<>();
        sortingKey = new ArrayList<>();
    }

    @Override
    protected DashboardEntity doInBackground(String... strings) {
        try {
            if(strings.length == 3 && strings[2] != ""){
                this.response = new JSONObject(strings[2]);

            }else {
                this.response = CommonManager.getResponseData(strings[0]);
            }

            String sort = "";

            if(strings.length == 3 && strings[1] != ""){
                sort = strings[1];
                this.dboard.setSort(sort);
            }

            JSONObject responseData = response.getJSONObject("ResponseData");

            if(responseData != null) {
                JSONArray jsonArray = responseData.getJSONArray("Data");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject block = (JSONObject) jsonArray.get(i);

                    switch (block.getInt("Type")) {
                        case 7:
                            addSortListBlock(block, sort);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.dboard;
    }

    @Override
    protected void onPostExecute(DashboardEntity blockList) {
        super.onPostExecute(blockList);

        try {

            for (BlockEntity b : blockList.getBlockList()) {

                switch (b.Type()){
                    case 7:
                        setSortListBlock(b);
                        setSortingOptions(this.response.toString(), blockList.Sort());
                        break;
                }
            }

            loading.setVisibility(View.GONE);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            //context.startActivity(new Intent(context, NoServiceActivity.class));
        }
    }

    private void addSortListBlock(JSONObject block, String sort){

        try {

            BlockEntity be = new BlockEntity(block.getInt("Type"),
                    0,
                    "");

            JSONArray contents = block.getJSONArray("Contents");

            //adding sorting info
            JSONArray sorting = ((JSONObject)contents.get(0)).getJSONArray("Sorting");
            for (int s = 0; s < sorting.length(); s++) {
                JSONObject info = ((JSONObject) sorting.get(s));

                sortingText.add(info.getString("Description"));
                sortingKey.add(info.getString("Key"));
            }

            //adding nodes
            JSONArray nodes = ((JSONObject)contents.get(0)).getJSONArray("Nodes");
            for (int n = 0; n < nodes.length(); n++) {
                JSONObject node = ((JSONObject) nodes.get(n));

                FrameEntity event = new FrameEntity(node.getInt("ID"), node.getString("Title"), node.getJSONArray("Images").get(0).toString(),
                        ((JSONObject)node.getJSONArray("SubTitle").get(0)).getString("Text"));

                try {
                    event.setDate(node.getInt("Date"));
                }catch (Exception ex){ }

                try {
                    event.setPrice(node.getInt("Price"));
                }catch (Exception ex){ }

                try {
                    event.setDifficulty(node.getInt("Difficulty"));
                    event.setDifficultyLabel(((JSONObject) node.getJSONArray("SubTitle").get(1)).getString("Text"));
                }catch (Exception ex){ }

                try {
                    event.setDistance(node.getDouble("Distance"));
                    event.setDistanceLabel(((JSONObject) node.getJSONArray("SubTitle").get(0)).getString("Text"));
                }catch (Exception ex){ }

                 be.FrameList().add(event);
            }

            if(sort.contentEquals("Date")) {
                //sortList
                Collections.sort(be.FrameList(), new Comparator<FrameEntity>() {
                    @Override
                    public int compare(FrameEntity o1, FrameEntity o2) {
                        return o1.Date() < o2.Date() ? -1
                                : o1.Date() > o2.Date() ? 1
                                : 0;
                    }
                });
            }else if(sort.contentEquals("Price")){
                Collections.sort(be.FrameList(), new Comparator<FrameEntity>() {
                    @Override
                    public int compare(FrameEntity o1, FrameEntity o2) {
                        return o1.Price() < o2.Price() ? -1
                                : o1.Price() > o2.Price() ? 1
                                : 0;
                    }
                });
            }else if(sort.contentEquals("Local")){
                /*Collections.sort(be.FrameList(), new Comparator<FrameEntity>() {
                    @Override
                    public int compare(FrameEntity o1, FrameEntity o2) {
                        return o1.Local().compareTo(o2.Local());
                    }
                });*/
            }else if(sort.contentEquals("Difficulty")){
                Collections.sort(be.FrameList(), new Comparator<FrameEntity>() {
                    @Override
                    public int compare(FrameEntity o1, FrameEntity o2) {
                        return o1.Difficulty() < o2.Difficulty() ? -1
                                : o1.Difficulty() > o2.Difficulty() ? 1
                                : 0;
                    }
                });
            }else if(sort.contentEquals("Distance")){
                Collections.sort(be.FrameList(), new Comparator<FrameEntity>() {
                    @Override
                    public int compare(FrameEntity o1, FrameEntity o2) {
                        return o1.Distance() < o2.Distance() ? -1
                                : o1.Distance() > o2.Distance() ? 1
                                : 0;
                    }
                });
            }else{
                Collections.sort(be.FrameList(), new Comparator<FrameEntity>() {
                    @Override
                    public int compare(FrameEntity o1, FrameEntity o2) {
                        return o1.Title().compareTo(o2.Title());
                    }
                });
            }

            dboard.getBlockList().add(be);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //region Render Block Types to Dashboard
    private void setSortListBlock(final BlockEntity b){
        /*mainContent.removeAllViews();

        View block = View.inflate(context, R.layout.spotlight_block, null);

        TextView title = block.findViewById(R.id.spotlight_block_title);
        title.setText(b.Title());

        LinearLayout wrapper = block.findViewById(R.id.spotlight_block_views);

        for (final FrameEntity f:b.FrameList()) {
            View frame = View.inflate(context, R.layout.slide, null);

            TextView frameDate = frame.findViewById(R.id.frame_date);
            TextView frameTitle = frame.findViewById(R.id.frame_title);
            LinearLayout routeInfo = frame.findViewById(R.id.event_route_briefing);

            if(f.Difficulty() > 0 && f.Distance() > 0){

                ImageView imgDistance = frame.findViewById(R.id.event_route_distance_image);
                imgDistance.setColorFilter(Color.parseColor(Settings.color));


                TextView distance = frame.findViewById(R.id.event_route_distance);
                distance.setText(f.DistanceLabel());
                distance.setTextColor(Color.parseColor(Settings.color));

                ImageView imgDifficulty = frame.findViewById(R.id.event_route_difficulty_image);
                imgDifficulty.setColorFilter(Color.parseColor(Settings.color));

                TextView difficulty = frame.findViewById(R.id.event_route_difficulty);
                difficulty.setText(f.DifficultyLabel());
                difficulty.setTextColor(Color.parseColor(Settings.color));

                frameDate.setVisibility(View.GONE);
                routeInfo.setVisibility(View.VISIBLE);
            }else {
                frameTitle.setVisibility(View.VISIBLE);
                frameTitle.setText(f.Title());

                frameDate.setTextColor(Color.parseColor(Settings.color));
                if(f.DisplayDate() != "null") {
                    frameDate.setText(f.DisplayDate());
                }else{
                    frameDate.setText("");
                }
            }

            ImageView img = frame.findViewById(R.id.frame_image);
            img.setImageBitmap(f.Image());

            if(f.Nid() > 0) {
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent event = new Intent(context, EventActivity.class);
                        int nid = f.Nid();
                        event.putExtra("nid", nid);
                        context.startActivity(event);
                    }
                });
            }




            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(0, 0, 0, 100);

            wrapper.addView(frame, layoutParams);
        }

        mainContent.addView(block);*/
    }

    private void setSortingOptions(final String data, final String sort){

        /*LinearLayout sortListType = sorting.findViewById(R.id.sorting_content_type);

        sortListType.removeAllViews();

        boolean first = true;

        for(int s= 0; s < sortingText.size(); s++){
            View option = View.inflate(context, R.layout.content_search_title, null);

            TextView title = option.findViewById(R.id.sort_title);
            title.setText(sortingText.get(s));

            final int _s = s;
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent event = new Intent(context, CategoryListActivity.class);
                    event.putExtra("sort", sortingKey.get(_s));
                    event.putExtra("data", data);
                    event.addFlags(FLAG_ACTIVITY_NO_HISTORY);
                    context.startActivity(event);
                }
            });

            if((sort != null && sort.contentEquals(sortingKey.get(_s))) || (sort == null && first)){
                title.setTextColor(Color.parseColor(Settings.color));
                first = false;
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(30, 0, 0, 0);

            sortListType.addView(option, layoutParams);
        }

        if(sortingText.size() > 0){
            sorting.setVisibility(View.VISIBLE);
        }else{
            sorting.setVisibility(View.GONE);
        }
    }

    /*private void sort(){
        mainContent.removeAllViews();

        Collections.sort(blockList.getBlockList(), new Comparator<BlockEntity>() {
            @Override
            public int compare(BlockEntity o1, BlockEntity o2) {
                return o1.Weight() < o2.Weight() ? -1
                        : o1.Weight() > o2.Weight() ? 1
                        : 0;
            }
        });

        View block = View.inflate(context, R.layout.spotlight_block, null);

        LinearLayout wrapper = block.findViewById(R.id.spotlight_block_views);

        for (final FrameEntity f:dboard.getBlockList().get(0).FrameList()) {
            View frame = View.inflate(context, R.layout.slide, null);

            TextView frameTitle = frame.findViewById(R.id.frame_title);
            frameTitle.setText(f.Title());

            ImageView img = frame.findViewById(R.id.frame_image);
            img.setImageBitmap(f.Image());

            if(f.Nid() > 0) {
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent event = new Intent(context, EventActivity.class);
                        int nid = f.Nid();
                        event.putExtra("nid", nid);
                        context.startActivity(event);
                    }
                });
            }

            TextView frameDate = frame.findViewById(R.id.frame_date);
            frameDate.setTextColor(Color.parseColor(Settings.color));
            if(f.DisplayDate() != "null") {
                frameDate.setText(f.DisplayDate());
            }else{
                frameDate.setText("");
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(0, 0, 0, 100);

            wrapper.addView(frame, layoutParams);
        }

        mainContent.addView(block);*/
    }
}
