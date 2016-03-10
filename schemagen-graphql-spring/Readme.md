This module includes a Spring Expression Language (EL) Datafetcher. 

----

The primary purpose of this Data fetcher is to enable us to be Lazy!. As in hibernate style lazy fetching meets GraphQL.

In this page we are going to talk about being lazy. As in *do nothing you don't have to*, or more specifically don't fetch a large number of relationships when you don't need them. 

First off let's assume you are going to implement a Calendar application that has two views, A month view and a Week view of the following graph:

![](https://github.com/bpatters/schemagen-graphql/blob/master/wiki/images/sampleGraph.png?raw=true)

In the graph above there are several 0..* potentially high plurality relationships. Let's consider the first relationship

Calendar ----> CalendarItem


This is what we call a top level query, and such you would likely handle it simple by exposing a Calendar top level query of:

    @GraphQuery(name="getCalendarItems")
    List<CalendarItem> getCalendarItems(@GraphQLParam("startDate") Date startDate, @GraphQLParam("endDate") Data endDate);


Now let's say you want to populate a Month view which has the following items to display:
* Title
* Date and time of the meeting

You would simply write a query like

    query {
       Calendar {
           getCalendarItems(startDate:1/1/2016, endDate:"1/31/2016") {
                  title
                  dateAndTime
           }
       }
    }


This would call the getCalendarItems endpoint and return only the title and dateTime fields for all calendar items. Not bad, if you had a 100 calendar items for the month and 50 guests for each calendar item (you're a popular person!) you would get  reasonable data set of a list of

    [ {"title":"title1", "dateAndTime":"1/1/2016"} ... ]

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


When the method is executed the method is going to call some business methods and return a List<CalendarItem> objects which will be used to populate these DTO's. The GraphQL layer will then extract the fields requests and return them to the UI. The problem here is the business layer has no idea what fields the UI is going to request, and as such it has to following some convention.

The simplest convention is the business layer fetches everything and fully populates all DTO's, and their relationships,  and lets the GraphQL layer reduce it before sending to to the UI. This works, but it still requires the backend to do an awful lot of extra data fetching. What if you had 100 calendar items in the month, with 50 guests each? That's 5000 User records you'd be fetching even though for the month view query we don't need them. 

What we want to do instead is find a way to minimize the backend fetching. The way we do this in Schemagen-GraphQL is by being lazy where it counts. By convention we want to have our business layer:

> Only fetch low plurality fields by default and lazy fetch the high plurality fields

How does this happen? It requires two things:
1. Implement your business layer to only fetch the fields of objects and leave out the connection by default
2. Annotate your high plurality fields in the DTO's with a LazyFetching data fetcher.

Currently, the only data fetcher that supports this property is: the 

    SpringELDataFetcher

This data fetcher allows you specify a Spring Expression Language that knows how to fetch the relationship that it annotates. For example, let's say you have a Spring bean that you've registered that knows how to fetch Guests for a specific calendar item. Let's call this bean:

    @Component("calendarManager")
    public class CalendarManager {
       List<User> getCalendarItemGuests(String calendarItemId);
    }


We could then annotate the CalendarItemDTO for lazy fetching with

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

Now we have a backend that will never fetch  calendar Items guest list unless the UI specifically requests it, in that scenario we will lazily fetch the guest list indirectly via the execution of the Spring EL expression. Let's break the expression down, in case your not familiar with it's usage. 

* @calendarManager -- The @ means that the variable after it is the name of a Registered Bean. In this case *calendarManager*
* .getCalenarItemGuests specifies the method to execute.
* (#this.id) - is the parameters to pass to the method. #this is pre-defined variable representing the wrapping object, in this case the Calendar Item and we use it to pass in the CalendarItem's ID field to the method.

That's it, you now have a lazy fetching enabled Graph!

# Implementation Steps

* include schemagen-graphql-spring in your dependencies.
* When building your schema register the SpringDataFetcherFactory as your default data fetching factory.

    GraphQLSchemaBuilder.registerDataFetcherFactory(new SpringELDataFetcherFactory());
    
* Annotate the methods you want to be lazily fetch with a spring expression data fetcher.