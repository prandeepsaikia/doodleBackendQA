package doodle.qa.api.regression.config;

public interface Endpoints {

    // USER ENDPOINTS
    String CREATE_USER = "";
    String DELETE_USER = "/{userId}";
    String UPDATE_USER = "/{userId}";
    String GET_USER_BY_ID = "/{userId}";
    String GET_ALL_USERS = "";
    String ADD_CALENDAR_TO_USER = "/{userId}/calendars/{calendarId}";
    String DELETE_CALENDAR_FROM_USER = "/{userId}/calendars/{calendarId}";


    // MEETING ENDPOINTS
    String CREATE_MEETING = "";
    String DELETE_MEETING = "/{id}";
    String UPDATE_MEETING = "/{id}";
    String GET_MEETING_BY_ID = "/{id}";
    String GET_ALL_MEETINGS = "";
    String GET_MEETING_TIME_SLOTS = "/slots";


// ##################################################################################
    // PROVIDER ENDPOINTS
    String GET_CALENDAR_BY_ID = "/{calendarId}";
    String UPDATE_CALENDAR = "/{calendarId}";
    String DELETE_CALENDAR = "/{calendarId}";
    String GET_ALL_CALENDARS = "";
    String CREATE_CALENDAR = "";

    // EVENT ENDPOINTS
    String GET_EVENT_BY_ID = "/{eventId}";
    String UPDATE_EVENT = "/{eventId}";
    String DELETE_EVENT = "/{eventId}";
    String CREATE_EVENT = "";
    String GET_EVENTS_BY_CALENDAR_ID = "/calendar/{calendarId}";
    String GET_EVENTS_BY_CALENDAR_ID_TIME_RANGE = "/calendar/{calendarId}/timerange";


}
