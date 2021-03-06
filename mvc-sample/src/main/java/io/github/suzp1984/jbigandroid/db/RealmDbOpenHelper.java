package io.github.suzp1984.jbigandroid.db;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.github.suzp1984.jbigandroid.realmobj.JbigItem;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by moses on 8/31/15.
 */
public class RealmDbOpenHelper implements DataBaseHelper {

    private Realm mRealm;

    @Inject
    public RealmDbOpenHelper(Realm realm) {
        mRealm = realm;
    }

    @Override
    public List<byte[]> getJbigs() {
        List<byte[]> ret = new ArrayList<>();

        RealmQuery<JbigItem> query = mRealm.where(JbigItem.class);
        RealmResults<JbigItem> results = query.findAll();

        for (JbigItem item : results) {
            ret.add(item.jbig);
        }

        return ret;
    }

    @Override
    public byte[] getJbig(int position) {
        RealmQuery<JbigItem> query = mRealm.where(JbigItem.class);
        RealmResults<JbigItem> results = query.findAll();

        JbigItem item = results.get(position);

        if (item != null) {
            return item.jbig;
        }

        return null;
    }

    @Override
    public void put(byte[] jbig) {
        Log.d("ReamlDb", "save realm object.");

        mRealm.beginTransaction();

        JbigItem item = mRealm.createObject(JbigItem.class);
        item.tag = "PaintView";
        item.jbig = jbig;

        mRealm.commitTransaction();
    }

    @Override
    public void put(Collection<byte[]> jbigs) {
        for(byte[] item : jbigs) {
            put(item);
        }
    }

    @Override
    public void delete(byte[] jbig) {
        RealmQuery<JbigItem> query = mRealm.where(JbigItem.class);
        RealmResults<JbigItem> results = query.findAll();

        for (JbigItem item : results) {
            if (item.jbig == jbig) {
                mRealm.beginTransaction();
                item.deleteFromRealm();
                mRealm.commitTransaction();
                break;
            }
        }
    }

    @Override
    public void delete(int position) {
        RealmQuery<JbigItem> query = mRealm.where(JbigItem.class);
        RealmResults<JbigItem> results = query.findAll();

        results.remove(position);

        mRealm.commitTransaction();
    }

    @Override
    public void deleteAll() {
        RealmQuery<JbigItem> query = mRealm.where(JbigItem.class);
        RealmResults<JbigItem> results = query.findAll();

        results.clear();
        mRealm.commitTransaction();
    }
}
