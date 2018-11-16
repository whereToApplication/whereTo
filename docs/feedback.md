# Feedback

Used to collect a Feedback from anonymous user

**URL** : `/api/feedback`

**Method** : `POST`

**Auth required** : NO

**Data constraints**

```json
{
    "feedback": "[valid non-empty feedback]",
}
```

**Data example**

```json
{
    "feedback": "I've never loved an application more than WhereTo",
}
```

## Success Response

**Code** : `200 OK`

**Content example**

```json
{
    "success": "Feedback was a success!",
    "response": "{
                  _id: 5bee759bb8a3661bf0d7213a,  
                  user: 'You',  
                  feedback: 'testing feedback',
                  __v: 0
                  }"
}
```

## Error Response

**Condition** : Feedback is less than 5 characters or empty

**Code** : `401 BAD REQUEST`

**Content** :

```json
{
  "error": "Missing feedback or feedback was less than 5 characters"
}
```
