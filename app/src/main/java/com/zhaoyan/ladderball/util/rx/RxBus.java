package com.zhaoyan.ladderball.util.rx;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * 基于RxJava实现的EventBus功能
 * Created by Yuri on 2015/11/10.
 */
public class RxBus {
    private static RxBus mInstance;

    public static synchronized RxBus get() {
        if (mInstance == null) {
            mInstance = new RxBus();
        }
        return mInstance;
    }

    private RxBus() {
    }

    private ConcurrentHashMap<Object, List<Subject>> subjectManager = new ConcurrentHashMap<>();

    public <T> Observable<T> register(Object tag, Class<T> clazz) {
        List<Subject> subjectList = subjectManager.get(tag);
        if (subjectList == null) {
            subjectList = new ArrayList<>();
            subjectManager.put(tag, subjectList);
        }

        Subject<T, T> subject;
        subjectList.add(subject = PublishSubject.create());
        return  subject;
    }

    public void unregister(Object tag, Observable observable) {
        List<Subject> subjectList = subjectManager.get(tag);
        if (subjectList != null) {
            subjectList.remove(observable);
            if (subjectList.isEmpty()) {
                subjectManager.remove(tag);
            }
        }
    }

    public void post(Object content) {
        post(content.getClass().getName(), content);
    }

    public void post(Object tag, Object content) {
        List<Subject> subjectList = subjectManager.get(tag);

        if (!subjectList.isEmpty()) {
            for (Subject subject : subjectList) {
                subject.onNext(content);
            }
        }
    }

}
