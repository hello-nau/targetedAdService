
## Background

ATA's AdvertisingService serves advertisements for ATA. These advertisements show up on the retail website and use
targeting to present different ATA advertisements to each individual. The targeting tries to take advantage of what
Amazon knows about you to show you the particular ad that is most likely to appeal to you.
## Use Cases

* As an ATA business owner, I want to create ATA advertising content that targets specific groups of customers.
* As an ATA business owner, I want to be able to update existing advertising content .
* As an ATA business owner, I want to be able to add a new targeting group to an existing piece of advertising content.
* As an ATA business owner, I want to generate an advertisement for a customer based on the existing advertising content.
* As an ATA business owner, I want to update an advertisement's click through rate for a targeting group. 
* ## Advertising Service Implementation Notes

A piece of content can have multiple targeting groups. For example, we can have a specific advertisement that is
displayed for customers who have spent at least $15 in the technical books category, but no more than $100 ***OR***
customers who have spent at least $15 on logic books. Every targeting rule within a targeting group is AND'd together.
To reach customers that meet one criteria *or *another, we have to use multiple targeting groups.

The click through rate tells us the probability that someone who sees this ad will click on it (values are from 0 to 1
inclusively). We separate click through rate by targeting group, because this advertisement may be incredibly popular
with customers who have bought technical books (30% chance they will click on the ad), but less popular with the logic
book group (only a 15% chance they will click on the ad).

To evaluate these targeting rules, we often have to call other services to get data like the PrimeClubService to find
out a customer's Prime benefits, or the CustomerService to get profile information and spending habits for the customer.

If, for a given customer, there is no eligible advertisement, we will return an empty ad. If any exception occurs when
calling to generate the ad, we do not want to throw an exception that will bubble up when rendering a detail page, so
instead we return an empty ad i.e. an empty string.

