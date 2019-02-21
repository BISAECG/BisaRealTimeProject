package com.bisa.health;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bisa.health.adapter.ContactAdapter;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.cust.view.ActionBarView;
import com.bisa.health.dao.IEventDao;
import com.bisa.health.model.Event;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.ResultData;
import com.bisa.health.model.User;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.GsonUtil;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ContactMainActivity extends BaseActivity {

    private IEventDao eventDao;
    private int orderNumber;
    private ListView lvContact;
    private ContactAdapter contactAdapter;
    private SharedPersistor sharedPersistor;
    private List<Event> listEvent;
    private RelativeLayout rl_tip;
    private IRestService restService;
    private User mUser;
    private HealthServer mHealthServer;
    ActionBarView actionBarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_contact_activity);
        AppManager.getAppManager().addActivity(this);
        sharedPersistor=new SharedPersistor(this);
        mUser=sharedPersistor.loadObject(User.class.getName());
        mHealthServer=sharedPersistor.loadObject(HealthServer.class.getName());
        restService = new RestServiceImpl(this,mHealthServer);
        actionBarView=findViewById(R.id.abar_title);
        actionBarView.setOnActionClickListenerNext(new ActionBarView.OnActionClickListener() {
            @Override
            public void onActionClick() {
                Log.i(TAG, "onClick: >>>>>>>>>>>>>>>>>>");
                Intent i = new Intent(ContactMainActivity.this, AddContactActivity.class);
                ActivityUtil.startActivity(ContactMainActivity.this,i,false, ActionEnum.NEXT);
            }
        });

        rl_tip= (RelativeLayout) this.findViewById(R.id.rl_tip);
        lvContact = (ListView) this.findViewById(R.id.list_contact);
        contactAdapter=new ContactAdapter(this);
        lvContact.setAdapter(contactAdapter);
        lvContact.setOnItemClickListener(listClickListener);


    }

    private static final String TAG = "ContactMainActivity";
    @Override
    protected void onResume() {
        super.onResume();

        listEvent=sharedPersistor.loadObject(Event.class.getName()+"-"+mUser.getUser_guid());
        Log.i(TAG, "onResume: "+(listEvent==null));
        if(listEvent!=null){

            if(listEvent!=null&&listEvent.size()>0){
                rl_tip.setVisibility(View.GONE);
                contactAdapter.setListEvent(listEvent);
                contactAdapter.notifyDataSetChanged();
            }else{
                rl_tip.setVisibility(View.VISIBLE);
                contactAdapter.clearList();
                contactAdapter.notifyDataSetChanged();

            }
        }else{
            syncSos();
        }


    }


    private AdapterView.OnItemClickListener listClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position < 0) {
                return;
            }
            Event event = contactAdapter.getEvent(position);
            Intent i = new Intent(ContactMainActivity.this, AddContactActivity.class);
            i.putExtra(Event.class.getName(),event);
            ActivityUtil.startActivity(ContactMainActivity.this,i,false, ActionEnum.NEXT);
        }

    };

    private void syncSos(){

        Call call=restService.getContacts();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();

                Log.i(TAG, "onResponse: "+json);

                final ResultData<List<Event>> result = GsonUtil.getInstance().parse(json,new TypeToken<ResultData<List<Event>>>(){}.getType());

                if (result == null) {
                    return;
                }
                if(result.getCode()== HttpFinal.CODE_200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listEvent=result.getData();
                            contactAdapter.setListEvent(listEvent);
                            contactAdapter.notifyDataSetChanged();
                            sharedPersistor.saveObject(Event.class.getName()+"-"+mUser.getUser_guid(),result.getData());
                        }
                    });


                }

            }
        });

    }
}
