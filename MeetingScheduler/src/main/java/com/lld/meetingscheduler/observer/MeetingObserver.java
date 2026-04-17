package com.lld.meetingscheduler.observer;

import com.lld.meetingscheduler.model.Meeting;

public interface MeetingObserver {
    void onMeetingScheduled(Meeting meeting);

    void onMeetingCancelled(Meeting meeting);
}
