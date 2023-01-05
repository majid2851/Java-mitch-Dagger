package com.example.java_unittest_mitch.repository;

import androidx.annotation.NonNull;

import com.example.java_unittest_mitch.models.Note;
import com.example.java_unittest_mitch.persistence.NoteDao;
import com.example.java_unittest_mitch.ui.Resource;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class NoteRepository
{
    public static final String NOTE_TITLE_NULL = "Note title cannot be null";
    public static final String INVALID_NOTE_ID = "Invalid id. Can't delete note";
    public static final String DELETE_SUCCESS = "Delete success";
    public static final String DELETE_FAILURE = "Delete failure";
    public static final String UPDATE_SUCCESS = "Update success";
    public static final String UPDATE_FAILURE = "Update failure";
    public static final String INSERT_SUCCESS = "Insert success";
    public static final String INSERT_FAILURE = "Insert failure";

    private int timeDelay=0;
    private TimeUnit timeUnit=TimeUnit.SECONDS;



    @NonNull
    private final NoteDao noteDao;

    @Inject
    public NoteRepository(@NonNull NoteDao noteDao)
    {
        this.noteDao = noteDao;
    }


    public Flowable<Resource<Integer>> insertNote(final Note note) throws Exception
    {
        checkTitle(note);
        return noteDao.insertNote(note)
                .delaySubscription(timeDelay,timeUnit)
                .map(new Function<Long, Integer>()
                {
                    @Override
                    public Integer apply(Long aLong) throws Exception
                    {
                        long l=aLong;
                        return (int)l;
                    }
                }).onErrorReturn(new Function<Throwable, Integer>()
                {
                    @Override
                    public Integer apply(Throwable throwable) throws Exception
                    {
                        
                        return -1;
                    }
                }).map(new Function<Integer, Resource<Integer>>()
                {
                    @Override
                    public Resource<Integer> apply(Integer integer) throws Exception
                    {
                        if (integer>0){
                            return Resource.success(integer,INSERT_SUCCESS);
                        }
                        return Resource.error(null,INSERT_FAILURE);
                    }
                }).subscribeOn(Schedulers.io()).toFlowable();
    }

    private void checkTitle(Note note) throws Exception
    {
        if (note.getTitle()==null)
        {
            throw new Exception(NOTE_TITLE_NULL);
        }
    }


}
