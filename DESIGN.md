# News App

*This document will serve as a design plan and as a requirement tracker for my own use.*

## Requirements

### Status 1

**Login Activity**  
- TextField Username  
- TextField Password  
- Button Enter  

---

**Home Activity**  
| Feature | Description |
|---------|-------------|
| **TextField Search Term** | A TextField will allow the user to input a desired search term. |
| **Button Search** | A Button will allow the user to navigate to a new screen to optionally fine-tune details about their search before viewing results. |
| **Empty Search Term** | If the user has not entered a search term, the Search button must be disabled. |
| **Local News Button** | A Button will allow the user to navigate to a new screen to view local news for a desired location. |
| **Top Headlines Button** | A Button will allow the user to navigate to a new screen to view top headlines. |
| **Data Persistence** | Save: (1) The search term, if one was entered. It must be restored (pre-populated in the EditText) upon the next launch. |

---

**Sources Activity**
| Requirement           | Description |
|-----------------------|-------------|
| **Search Term**       | The search term the user had typed on the previous screen should be listed on this screen (e.g. “Search for: ‘Android’”). |
| **Sources Categories** | Use a Spinner or composable equivalent to give the user a dropdown menu of all the supported categories of the News API.<br>See the Sources API documentation for the list of supported categories.<br>For example, “health” is a supported category. |
| **Sources Networking** | Automatically load news sources into a composable list for the category selected in the Spinner.<br>This will use the Sources API.<br>Each news source entry should indicate the name & description of the source. |
| **Changing Sources**   | If a new category is selected, update the list of sources. |
| **Source Selection**   | Upon clicking a source, advance to the next screen to display results for that source + search term. |
| **Skip Sources**       | A Button to allow the user to skip source selection should be given.<br>If the user selects this button, immediately advance to the next screen to display results for their search term, without filtering the results by any particular source. |
