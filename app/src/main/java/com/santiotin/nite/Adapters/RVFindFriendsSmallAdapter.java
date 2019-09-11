package com.santiotin.nite.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.santiotin.nite.Activities.FindFriendsActivity;
import com.santiotin.nite.Activities.PersonProfileActivity;
import com.santiotin.nite.Models.User;
import com.santiotin.nite.Parsers.SnapshotParserUser;
import com.santiotin.nite.R;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RVFindFriendsSmallAdapter extends RecyclerView.Adapter<RVFindFriendsSmallAdapter.ViewHolder> {


    private List<User> users;
    private int layout;
    private OnItemClickListener itemClickListener;
    private Context c;

    public RVFindFriendsSmallAdapter(List<User> users, int layout, OnItemClickListener listener, Context c) {
        this.users = users;
        this.layout = layout;
        this.itemClickListener = listener;
        this.c = c;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(users.get(position), itemClickListener, c);
    }

    @Override
    public int getItemCount() {
        // return tamaño de la lista que tenemos
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // Elementos UI a rellenar
        TextView name;
        ImageView image;
        public Button follow;

        Boolean leSigo;

        private FirebaseFirestore db;
        private FirebaseAuth mAuth;
        private FirebaseUser fbUser;

        ViewHolder(final View itemView) {
            // Recibimos la vista completa. La pasa al constructor padre y enlazamos referencias UI
            // con nuestras propiedades ViewHolder declarados justo arriba
            super(itemView);

            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            fbUser = mAuth.getCurrentUser();

            leSigo = false;

            name = itemView.findViewById(R.id.tvname);
            image =  itemView.findViewById(R.id.imgViewFriend);
            follow = itemView.findViewById(R.id.follow);

        }

        void bind(final User u, final OnItemClickListener listener, final Context context){
            // Procesamos los datos a rellenar
            Long photoTime = System.currentTimeMillis() / (1000*60*60);
            name.setText(u.getName());
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profilepics/" + u.getUid() + ".jpg");
            GlideApp.with(context)
                    .load(storageRef)
                    .signature(new ObjectKey(photoTime))
                    .error(R.drawable.logo)
                    .into(image);

            consultarRelacion(u);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(u, getAdapterPosition());
                }
            });

            follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    consultarRelacion(u);
                    if (!leSigo){
                        followFriend(u);
                    } else {
                        unfollowFriend(u);
                    }
                }
            });

        }

        private void consultarRelacion(User friendUser) {

            db.collection("users")
                    .document(fbUser.getUid())
                    .collection("following")
                    .document(friendUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                leSigo = true;
                                Log.d("control", "DocumentSnapshot data: " + documentSnapshot.getData());
                            }else{
                                leSigo = false;
                                Log.d("control", "No such document");
                            }
                            actualizarBoton();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("control", "Error", e);
                        }
                    });


        }

        private void actualizarBoton(){
            if(leSigo){
                follow.setText("Siguiendo");
                follow.setTextColor(itemView.getResources().getColor(R.color.white));
                follow.setBackground(itemView.getResources().getDrawable(R.drawable.rectangle_pink));
            }
            else{
                follow.setText("Seguir");
                follow.setTextColor(itemView.getResources().getColor(R.color.pink2));
                follow.setBackground(itemView.getResources().getDrawable(R.drawable.rectangle_white_pink));
            }

        }

        private void followFriend(final User friendUser){
            Map<String, Object> following = new HashMap<>();
            following.put("followingName", friendUser.getName());

            final Map<String, Object> follower = new HashMap<>();
            follower.put("followerName", fbUser.getDisplayName());

            //crear los 2 documentos
            db.collection("users")
                    .document(fbUser.getUid())
                    .collection("following")
                    .document(friendUser.getUid())
                    .set(following)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            db.collection("users")
                                    .document(friendUser.getUid())
                                    .collection("followers")
                                    .document(fbUser.getUid())
                                    .set(follower)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("control", "DocumentSnapshot successfully written!");
                                            //leSigo = true;
                                            transactionIncrementFriend(friendUser);
                                            sendRequestNotification(friendUser);
                                            consultarRelacion(friendUser);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("control", "Incoherencia!!", e);
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("control", "Error writing document", e);
                        }
                    });

        }

        private void unfollowFriend(final User friendUser){
            //borrar los 2 documentos
            db.collection("users")
                    .document(fbUser.getUid())
                    .collection("following")
                    .document(friendUser.getUid())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            db.collection("users")
                                    .document(friendUser.getUid())
                                    .collection("followers")
                                    .document(fbUser.getUid())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("control", "DocumentSnapshot successfully deleted!");
                                            transactionDecrementFriend(friendUser);
                                            consultarRelacion(friendUser);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("control", "Incoherencia!!!!!", e);
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("control", "Error deleting document", e);
                        }
                    });
        }

        private void transactionIncrementFriend(User friendUser){

            final DocumentReference sfDocRef1 = db.collection("users").document(fbUser.getUid());
            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(sfDocRef1);
                    double newPopulation = snapshot.getLong("numFollowing") + 1;
                    transaction.update(sfDocRef1, "numFollowing", newPopulation);

                    // Success
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("control", "Transaction success!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("control", "Transaction failure.", e);
                }
            });

            final DocumentReference sfDocRef2 = db.collection("users").document(friendUser.getUid());
            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(sfDocRef2);
                    double newPopulation = snapshot.getLong("numFollowers") + 1;
                    transaction.update(sfDocRef2, "numFollowers", newPopulation);

                    // Success
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("control", "Transaction success!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("control", "Transaction failure.", e);
                }
            });
        }

        private void transactionDecrementFriend(User friendUser){
            final DocumentReference sfDocRef1 = db.collection("users").document(fbUser.getUid());

            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(sfDocRef1);
                    double newPopulation = snapshot.getLong("numFollowing") - 1;
                    transaction.update(sfDocRef1, "numFollowing", newPopulation);

                    // Success
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("control", "Transaction success!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("control", "Transaction failure.", e);
                }
            });

            final DocumentReference sfDocRef2 = db.collection("users").document(friendUser.getUid());

            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(sfDocRef2);
                    double newPopulation = snapshot.getLong("numFollowers") - 1;
                    transaction.update(sfDocRef2, "numFollowers", newPopulation);

                    // Success
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("control", "Transaction success!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("control", "Transaction failure.", e);
                }
            });
        }

        private void sendRequestNotification(final User friendUser) {

            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            Timestamp timestamp = new Timestamp(c.getTimeInMillis());

            final Map<String, Object> notification = new HashMap<>();
            notification.put("personName", fbUser.getDisplayName());
            notification.put("day", day);
            notification.put("month", month+1);
            notification.put("year", year);
            notification.put("time", timestamp);

            db.collection("users")
                    .document(friendUser.getUid())
                    .collection("notFriendRequests")
                    .document(fbUser.getUid())
                    .set(notification)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("control", "Notificacion enviada");
                        }
                    });

        }
    }



    public interface OnItemClickListener{
        void onItemClick(User u, int position);
    }

}
