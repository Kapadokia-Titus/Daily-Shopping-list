package nyandoro.kapadokia.dailyshoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

import nyandoro.kapadokia.dailyshoppinglist.model.Data;

public class Home extends AppCompatActivity {

    //casting our toolbar
    private Toolbar toolbar;

    //implementing our recycler view
    private RecyclerView recyclerView;

    //declarations
    private FloatingActionButton fab_btn;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;

    //total ammount declaration
    private TextView totalsum;

    //creating global variables...
    private String type;
    private int ammount;
    private String note;
    private String post_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        totalsum = findViewById(R.id.total_ammount);
        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daily shopping list");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uId = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Shopping List ").child(uId);
        reference.keepSynced(true);

        recyclerView = findViewById(R.id.recycler_home);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        fab_btn = findViewById(R.id.fab);
        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog();
            }
        });

        //calculating total amount of our shopping ammount
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              //initializing the total to zero
                int totalammount=0;
                //tis gets all the children of ou database
                for (DataSnapshot snap:dataSnapshot.getChildren()){

                    Data data = snap.getValue(Data.class);
                    totalammount+=data.getAmmount();

                    //converting our amount to string so that we could display in TextView
                    String stTotal = String.valueOf(totalammount+".00");

                    totalsum.setText(stTotal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void customDialog() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(Home.this);
        LayoutInflater inflater = LayoutInflater.from(Home.this);
        View myview = inflater.inflate(R.layout.input_data, null);

        final AlertDialog dialog = mydialog.create();
        dialog.setView(myview);


        final EditText type = myview.findViewById(R.id.edt_type);
        final EditText ammount =  myview.findViewById(R.id.edt_ammount);
        final EditText note = myview.findViewById(R.id.edt_note);
        Button save = myview.findViewById(R.id.btn_save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mType = type.getText().toString().trim();
                String mAmmount = ammount.getText().toString().trim();
                String mNote = note.getText().toString().trim();

                //set our ammount to integer
                int amount =  Integer.parseInt(mAmmount);

                //checking if the fields are empty
                if (TextUtils.isEmpty(mType)){
                    type.setError("Required field");
                    return;
                }
                if (TextUtils.isEmpty(mAmmount)){
                    ammount.setError("Required");
                    return;
                }
                if (TextUtils.isEmpty(mNote)){
                    note.setError("Required");
                    return;
                }


                if (!TextUtils.isEmpty(mType) && !TextUtils.isEmpty(mAmmount) && !TextUtils.isEmpty(mNote)){
                    //an object of our module class

                    String id = reference.push().getKey();
                    String date = DateFormat.getDateInstance().format(new Date());
                    Data data = new Data(mType, amount, mNote, date, id);
                    reference.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Home.this, "Data added", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(Home.this, "error posting your data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }



                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        //implementing firebase recycler here
        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.item_data,
                MyViewHolder.class,
                reference
        ) {
            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, final Data data,final int position) {
                myViewHolder.setDate(data.getDate());
                myViewHolder.setType(data.getType());
                myViewHolder.setAmmount(data.getAmmount());
                myViewHolder.setNote(data.getNote());

                //setting my view holder to an onclick listener
                myViewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(position).getKey();
                        type = data.getType();
                        ammount = data.getAmmount();
                        note = data.getNote();
                        updateData();
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }

    //creating a static class for handling the recycler view
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        //create the object of our view class
        View myView;

        //create a constructor
        public  MyViewHolder (View itemView){
            super(itemView);
            myView = itemView;
        }

        //set type method
        public void setType(String type){
            TextView mType = myView.findViewById(R.id.type);
            mType.setText(type);
        }

        //set note method
        public void setNote(String note){
            TextView mNote = myView.findViewById(R.id.note);
            mNote.setText(note);
        }
        //set date
        public void setDate(String date){
            TextView mDate = myView.findViewById(R.id.date);
            mDate.setText(date);
        }
        //set ammount
        public void setAmmount(int amount){
            TextView mAmmount = myView.findViewById(R.id.ammount);
            String stam = String.valueOf(amount);
            mAmmount.setText(stam);
        }
    }


    //NEW method for handling update information
    public void updateData(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        LayoutInflater inflater = LayoutInflater.from(Home.this);

        View mView = inflater.inflate(R.layout.update_input, null);

        final AlertDialog dialog= alertDialog.create();
        dialog.setView(mView);

        final EditText ed_type = mView.findViewById(R.id.edt_type_upd);
        final EditText ed_ammount = mView.findViewById(R.id.edt_ammount_upd);
        final EditText ed_note = mView.findViewById(R.id.edt_note_upd);

        ed_type.setText(type);
        //set selection to start our typing indicator at the end of our text
        ed_type.setSelection(type.length());

        ed_ammount.setText(String.valueOf(ammount));//converting it to string first
        ed_ammount.setSelection(String.valueOf(ammount).length());//converting it to string first

        ed_note.setText(note);
        ed_note.setSelection(note.length());


        Button btn_delete = mView.findViewById(R.id.btn_delete_upd);
        Button btn_update = mView.findViewById(R.id.btn_save_upd);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //getting the string variables
                type = ed_type.getText().toString().trim();
                //convert ammount into string
                String mAmmount = String.valueOf(ammount);
                mAmmount = ed_ammount.getText().toString().trim();
                note = ed_note.getText().toString().trim();

                int intammount = Integer.parseInt(mAmmount);
                String date = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(type, intammount,note,date,post_key);
                reference.child(post_key).setValue(data);

                dialog.dismiss();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reference.child(post_key).removeValue();
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    //oncreate menu option

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    //add on option action selected menu

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
