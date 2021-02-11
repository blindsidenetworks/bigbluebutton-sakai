/**
 * Copyright (c) 2010 onwards - The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sakaiproject.bbb.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.TransientObjectException;
import org.hibernate.criterion.Restrictions;

import org.sakaiproject.bbb.api.BBBStorageManager;
import org.sakaiproject.bbb.api.storage.BBBMeeting;
import org.sakaiproject.component.api.ServerConfigurationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BBBStorageManagerImpl implements BBBStorageManager {

    @Resource private ServerConfigurationService serverConfigurationService;
    @Resource(name = "org.sakaiproject.springframework.orm.hibernate.GlobalSessionFactory")
    private SessionFactory sessionFactory;

    @Transactional
    public boolean storeMeeting(BBBMeeting meeting) {

        Session session = sessionFactory.getCurrentSession();
        BBBMeeting mergedMeeting = null;
        try {
            mergedMeeting = (BBBMeeting) session.merge(meeting);
        } catch (TransientObjectException toe) {
            session.persist(meeting);
        }
        return true;
    }

    @Transactional
    public boolean updateMeeting(BBBMeeting meeting, boolean updateParticipants) {

        sessionFactory.getCurrentSession().save(meeting);
        return true;
    }

    @Transactional
    public List<BBBMeeting> getSiteMeetings(String siteId, boolean includeDeleted) {

        Session session = sessionFactory.getCurrentSession();

        Criteria c = session.createCriteria(BBBMeeting.class)
            .add(Restrictions.eq("siteId", siteId));

        if (!includeDeleted) {
            c.add(Restrictions.ne("deleted", true));
        }

        return (List<BBBMeeting>) c.list();
    }

    @Transactional
    public BBBMeeting getMeeting(String meetingId) {
        return (BBBMeeting) sessionFactory.getCurrentSession().get(BBBMeeting.class, meetingId);
    }

    @Transactional
    public boolean deleteMeeting(String meetingId) {
        return deleteMeeting(meetingId, false);
    }

    @Transactional
    public boolean deleteMeeting(String meetingId, boolean fullDelete) {

        Session session = sessionFactory.getCurrentSession();

        if (fullDelete) {
            session.delete(session.get(BBBMeeting.class, meetingId));
        } else {
            BBBMeeting meeting = (BBBMeeting) session.get(BBBMeeting.class, meetingId);
            meeting.setDeleted(true);
            session.merge(meeting);
        }

        return true;
    }

    @Transactional
    public String getMeetingHost(String meetingId) {

        BBBMeeting meeting = (BBBMeeting) sessionFactory.getCurrentSession().get(BBBMeeting.class, meetingId);
        return meeting.getHostUrl();
    }

    @Transactional
    public List<BBBMeeting> getAllMeetings() {
        return (List<BBBMeeting>) sessionFactory.getCurrentSession().createCriteria(BBBMeeting.class).list();
    }

    @Transactional
    public boolean setMeetingHost(String meetingId, String hostUrl) {

        Session session = sessionFactory.getCurrentSession();

        BBBMeeting meeting = (BBBMeeting) session.get(BBBMeeting.class, meetingId);
        meeting.setHostUrl(hostUrl);
        session.merge(meeting);

        return true;
    }
}
