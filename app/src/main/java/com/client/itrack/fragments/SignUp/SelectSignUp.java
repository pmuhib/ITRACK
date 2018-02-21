package com.client.itrack.fragments.SignUp;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.activities.HomeContainer;
import com.client.itrack.fragments.RegisterFragments;
import com.client.itrack.fragments.client.AddClientFragments;

/**
 * Created by sony on 03-03-2017.
 */

public class SelectSignUp extends Fragment implements View.OnClickListener {
    TextView AsCompany,AsIndiviual;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.signupselection,container,false);
        AsCompany= (TextView) view.findViewById(R.id.txt_AddWithCompanyDomain);
        AsIndiviual= (TextView) view.findViewById(R.id.txt_AddAsIndividual);
        AsCompany.setOnClickListener(this);
        AsIndiviual.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if(id==R.id.txt_AddWithCompanyDomain)
        {
           SearchCompany registerFragments = new SearchCompany();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framecontainer, registerFragments).addToBackStack(null).commit();
        }
        else if(id==R.id.txt_AddAsIndividual)
        {
            Addindividual fragments=new Addindividual();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framecontainer,fragments).addToBackStack(null).commit();
          /*  RegisterFragments registerFragments = new RegisterFragments();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framecontainer, registerFragments).addToBackStack(null).commit();*/
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TextView)getActivity().findViewById(R.id.txt_heading)).setText("Sign Up");
        ((ImageView)getActivity().findViewById(R.id.btn_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity().getSupportFragmentManager().getBackStackEntryCount()>0)
                {
                    Intent intent = new Intent(getActivity(), HomeContainer.class); // LoginContainer.class
                    startActivity(intent);
                }
            }
        });
    }
}
