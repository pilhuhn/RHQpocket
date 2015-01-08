package org.rhq.pocket.operation;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.Toast;

import org.codehaus.jackson.JsonNode;

import org.rhq.pocket.FinishCallback;
import org.rhq.pocket.R;
import org.rhq.pocket.RHQActivity;
import org.rhq.pocket.TalkToServerTask;

/**
 * Base class for the operation activities
 * @author Heiko W. Rupp
 */
public abstract class AbstractOperationActivity extends RHQActivity {

    public void deleteCurrent(String historyId) {
        new TalkToServerTask(this,new FinishCallback() {
            @Override
            public void onSuccess(JsonNode result) {
                Toast.makeText(getApplicationContext(), getString(R.string.SuccessfullyDeleted), Toast.LENGTH_SHORT).show();
                refresh(null);

                enableMenuItem(R.id.trash_this, false);

            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(),getString(R.string.DeleteFailed) + e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        },"/operation/history/" + historyId,"DELETE").execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.trash_this) {

            FragmentManager fm = getFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.detail_container);
            if (fragment!=null && fragment instanceof OperationHistoryDetailFragment) {
                String historyId = ((OperationHistoryDetailFragment)fragment).getHistoryId();
                deleteCurrent(historyId);
                // remove the details fragment
                FragmentTransaction ft = fm.beginTransaction();
                ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.remove(fragment);
                ft.commit();

            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
