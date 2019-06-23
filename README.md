# contact-tasks

Add a task to a contact or view incomplete tasks for a contact

## Threat Model

**Do I need thread modeling checklist:**

```
[X] Storing/Displaying user input*
[ ] Display PII (Personally Identifying Information)
[X] Storing PII
[X] Transmitting PII
[ ] New Third Party resources are being introduced (Libraries, page redirects,etc)
[ ] Uploading files
[ ] Stealth Access
[ ] Proxies
```

**Is any of the following information stored, viewed, or transmitted?**

```
[X] Name
[X] Address
[X] Email Address
[X] Phone Number
[ ] Bank Information (routing, account number, etc)
[ ] Credit Card Information
[ ] Signatures (images of signatures, documents with signatures)
[ ] Tax identifiers such as SSN
[ ] Date of birth
[ ] Login information (username or password)
[ ] IP address
[ ] External account information
    [ ] Policy number
    [ ] Group number
    [ ] PIN numbers
    [ ] Login information
    [ ] API Keys and passwords
```

**If no security measures are in place list the possible threats. Who would likely attack our products?**

```
- Employees
- Developers of partners
```

**List how can these threats be enabled (vulnerabilities). What are the vulnerabilities in the feature?**

```
- Botnets
```

**List the possible motivations of an attacker. Why would the attacker use these vulnerabilities?**

```
- Data mining for spam lists
- Competitor information
```

**Map each attacker to the applicable vulnerabilities and rank the likelihood and impact of each vulnerabilities 1-5 where 1 is the lowest and 5 is the highest for determining order of importance.
When ranking likelihood ask yourself what are the chances of this happening?
When ranking impact ask yourself what would be the consequences to Infusionsoft be if this happened (financially and reputationally)?**

```
- Partners with access to API/Application Impact 2
```

**What are the countermeasures for each vulnerability (one sentence for each)?**

```
- Requiring valid credentials (Oauth, access token)
- Requiring registration and vetting process for developers accessing the API
```