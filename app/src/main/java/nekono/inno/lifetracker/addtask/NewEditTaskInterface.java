package nekono.inno.lifetracker.addtask;

import android.content.Context;
import android.widget.AdapterView;
import android.widget.TextView;

public interface NewEditTaskInterface {

    interface View {

        void setItems();
        void close();
        void startTimer();
    }

    interface Presenter {

        void onPlayPressed(Context context);

        void onItemSelected(int position, AdapterView<?> parent);

        void onAddPressed(TextView name, TextView category, TextView project, TextView comments, Context context, String taskName, long time);
    }
}
