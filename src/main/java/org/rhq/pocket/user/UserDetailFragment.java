package org.rhq.pocket.user;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.rhq.core.domain.rest.UserRest;
import org.rhq.pocket.FinishCallback;
import org.rhq.pocket.R;
import org.rhq.pocket.TalkToServerTask;

/**
 * Display details of a user
 * @author Heiko W. Rupp
 */
public class UserDetailFragment extends Fragment {

    private TableLayout tableLayout;
    private TextView loginView;
    private TextView firstNameView;
    private TextView lastNameView;
    private TextView emailView;
    private TextView phoneView;
    private String login;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tableLayout = (TableLayout) inflater.inflate(R.layout.user_detail_fragment,container,false);
        loginView = (TextView) tableLayout.findViewById(R.id.user_detail_login);
        firstNameView = (TextView) tableLayout.findViewById(R.id.user_detail_firstName);
        lastNameView = (TextView) tableLayout.findViewById(R.id.user_detail_lastName);
        emailView = (TextView) tableLayout.findViewById(R.id.user_detail_email);
        phoneView = (TextView) tableLayout.findViewById(R.id.user_detail_phone);
        return tableLayout;
    }

    public void setLogin(String login) {

        this.login = login;
    }

    @Override
    public void onResume() {
        super.onResume();

        new TalkToServerTask(getActivity(),new FinishCallback() {
            @Override
            public void onSuccess(JsonNode result) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                try {
                    UserRest user = objectMapper.readValue(result,new TypeReference<UserRest>() {});
                    fillFields(user);
                }
                catch (Exception e) {
                    e.printStackTrace(); // TODO
                }

            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(),"Getting user info failed: " + e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        },"/user/" + login).execute();
    }

    private void fillFields(UserRest user) {
        loginView.setText(user.getLogin());
        firstNameView.setText(user.getFirstName());
        lastNameView.setText(user.getLastName());
        emailView.setText(user.getEmail());
        phoneView.setText(user.getTel());
    }
}
