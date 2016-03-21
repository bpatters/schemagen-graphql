This module includes a Spring Expression Language (EL) Datafetcher. 

----

The primary purpose of this Data fetcher is to enable us bring the concept of Hibernate Lazy Fetches to GraphQL.

Let's assume you are going to implement a Calendar application that has two views, A month view and a Week view of the following graph:

![](https://github.com/bpatters/schemagen-graphql/blob/master/wiki/images/sampleGraph.png?raw=true)

In the graph above there are several 0..* potentially high plurality relationships. Let's consider the first relationship

Calendar ----> CalendarItem


This is what we call a top level relationship, or in other words it's an entrypoint into the graph.
For these you expose a query field on the top level static Calendar object like:

    @GraphQuery(name="calendarItems")
    List<CalendarItem> getCalendarItems(@GraphQLParam("startDate") Date startDate, @GraphQLParam("endDate") Data endDate);

This query can be used for both the week view, and the month view. It allows us to fetch all the calendar items for a specified date range.

Month View

----

Now let's say you want to populate a Month view, and due to limited space you only want to display two fields:
* Title
* Date and time of the meeting

To do this you would use the following GraphQL Query

    query {
       Calendar {
           calendarItems(startDate:"1/1/2016", endDate:"1/31/2016") {
                  title
                  dateAndTime
           }
       }
    }


This would call the getCalendarItems endpoint and return only the title and dateTime fields for all calendar items. Not bad, if you had a 100 calendar items for the month and 50 guests for each calendar item (you're a popular person!) you would get a list of JSON response payload of 100 calendar items like:

    { "data": [ {"title":"title1", "dateAndTime":"1/1/2016"} ... ] }
    

This would be a reasonable amount of data, and the minimal set, to return from the Backend to the UI. However, let's look a bit deeper on the backend and what it has to do to solve this request. Let's assume you have the following two classes representing the CalendarItem and it's Guests (users):
    
     public class CalendarItemDTO {
       String id;
       String title;
       DateTime dateAndTime;
       User owner;
       List<User> guests;
     }

     public class UserDTO {
       String id;
       String firstName;
       String lastName;
       String email;
     }


When the method is executed it is going to call some business methods which will return a List<CalendarItem> object which will be used to populate these DTO's. The GraphQL layer will then extract the fields requested and return them to the UI. The problem here is the business layer has no idea what fields the UI is going to request. GraphQL asks for the calendarItems and then goes through each field the UI requested and extracts them individually. Short of passing the query field graph to the business object, which would be extreme, we don't really have an easy way to explain to the business layer exactly what the UI is going to use from the result set. As such we have to fall back to conventions.

The simplest convention is the business layer fetches everything and fully populates all DTO's, and their relationships,  then lets the GraphQL layer reduce it before sending it to the UI. This works, but it still requires the backend to do an awful lot of extra data fetching. What if you had 100 calendar items in the month, with 50 guests each? That's 5000 User records you'd be fetching even though for the month view query we don't need any of the Guest information!!!

Ideally what we would want is for the backend to only fetch the *small and cheap to fetch data* and save the *large and expensive to fetch* data until we know for sure it will be used. The way we do this in Schemagen-GraphQL is by being lazy where it counts. By convention we want to:

> Only fetch low plurality fields by default and lazy fetch the high plurality fields

This means by convention anything that is a Collection, ie List, we don't populate by default but instead fetch only when explicitely asked to.
How does this happen? It requires two things:
1. Implement your business layer to only fetch the low plurality fields aka primitive fields and 1-to-1 relationship fields (you can further optimize by making all relationships lazy, but for this example we stick to high plurality fields).
2. Annotate your high plurality fields in the DTO's with a DataFetcher that supports lazy data fetching

Currently, the only data fetcher that supports this property is:

    GraphQLSpringELDataFetcher

This data fetcher allows you specify a Spring Expression Language expression that knows how to fetch the relationship that it annotates. For example, let's say you have a Spring bean that you've registered that knows how to fetch Guests for a specific calendar item. Let's call this bean:

    @Component("calendarManager")
    public class CalendarManager {
       List<UserDTO> getCalendarItemGuests(String calendarItemId);
    }


*This bean could also double as your root Calendar GraphController, which is very useful to be able to reuse it's functions*

We could then annotate the CalendarItemDTO for lazy fetching with:

    public class CalendarItemDTO {
       String id;
       String title;
       DateTime dateAndTime;
       User owner;
       List<UserDTO> guests;

       @GraphQLSpringELDataFetcher ("@calendarManager.getCalendarItemGuests(#this.id)")
       List<UserDTO> getGuests() {
           throw new IllegalArgument("Data fetcher failed, this property is lazy fetched!");
       }
     }

Now we have a backend that will never fetch a calendar items guest list unless the UI specifically requests it, in that scenario we will lazily fetch the guest list indirectly via the execution of the Spring EL expression. Let's break the expression down, in case your not familiar with it's usage. 

* @calendarManager The @ means that the variable after it is the name of a Registered Bean. In this case *calendarManager*
* .getCalenarItemGuests specifies the method to execute.
* (#this.id) - is the parameters to pass to the method. #this is pre-defined variable representing the wrapping object, in this case the Calendar Item and we use it to pass in the CalendarItem's ID field to the method.

That's it, you now have a lazy fetching enabled Graph!

# Implementation Steps

* Include schemagen-graphql-spring in your dependencies.
* When building your schema register the SpringDataFetcherFactory as your default data fetching factory. *this adds support for detection of the @GraphQLSpringELDataFetcher annotation during schema compilation.*

    GraphQLSchemaBuilder.registerDataFetcherFactory(new SpringELDataFetcherFactory());
    
* Annotate the methods you want to be lazily fetch with a spring expression data fetcher.