Test Cases:
**Clear files in between runs [get rid of ‘.txt’ files and stop and rerun server and client]

Methods:

Create a Seller:
1. User launches an application
2. User presses ‘OK’ on Welcome Message
3. User selects 2 for ‘Register’
4. User enter testBuyer@test.com and presses ‘OK’
  User enter testBuyer and presses ‘OK’
  User enter passwordBuyer and presses ‘OK’
  User enter ‘B’ and presses ‘OK’
  User gets ‘You are now logged in as testBuyer’

Create a Buyer:
  User launches an application
  User presses ‘OK’ on Welcome Message
  User selects 2 for ‘Register’
  User enter testSeller@test.com and presses ‘OK’
  User enter testSeller and presses ‘OK’
  User enter passwordSeller and presses ‘OK’
  User enter ‘S and presses ‘OK’
  User gets ‘You are now logged in as testSeller’

Test 1: User Register
Steps:
Create a Buyer
Check ‘account.txt’ file for credentials
Test 1 Status: PASSED

Test 2: User log in
Steps:
Create a Seller
User launches an application
User presses ‘OK’ on Welcome Message
User selects 1 for ‘Login’
User enter testSeller and presses ‘OK’
User enter passwordSeller and presses ‘OK’
User gets ‘You are now logged in as testSeller’
EXIT by pressing ‘X’
Test 2 Status: PASSED

Test 3: Buyer messages Seller through Stores
Steps:
Create a Seller
Seller selects 2 for ‘Create a Store’
Seller enters name of store as ‘testSellerStore’
EXIT by pressing ‘X’
Create a Buyer
Buyer selects 1 for ‘Message a Seller’
Buyer selects 1 for ‘See a list of Stores’
Buyer enters ‘testSellerStore’
Buyer selects 2 for ‘Send a new message’
Buyer selects 1 for ‘Send a message’
Buyer enters ‘hello’
EXIT by pressing ‘X’
Check ‘testBuyer-testSeller.txt’ file to see if message is sent
Test 3 Status: PASSED

Test 4: Buyer messages Seller through List
Steps:
Create a Seller
Seller selects 2 for ‘Create a Store’
Seller enters name of store as ‘testSellerStore’
EXIT by pressing ‘X’
Create a Buyer
Buyer selects 1 for ‘Message a Seller’
Buyer selects 2 for ‘Search for a Seller’
Buyer enters ‘testSeller’
Buyer selects 2 for ‘Send a new message’
Buyer selects 1 for ‘Send a message’
Buyer enters ‘hello’
EXIT by pressing ‘X’
Check ‘testBuyer-testSeller.txt’ file to see if message is sent
Test 4 Status: PASSED

Test 5: Seller messages Buyer through List
Steps:
Create a Buyer
EXIT by pressing ‘X’
Create a Seller
Seller selects 1 for ‘Search for a buyer to message’
Seller selects 1 for ‘See list of buyers’
Seller enters ‘testBuyer’
Seller selects 2 for ‘Send a new message’
Seller selects ‘Send a message’
Seller enters ‘hello’
Check ‘testBuyer-testSeller.txt’ file to see if message is sent
Test 5 Status: PASSED

Test 6: Seller messages Buyer through Search
Steps:
Create a Buyer
EXIT by pressing ‘X’
Create a Seller
Seller selects 1 for ‘Search for a buyer to message’
Seller selects 2 for ‘Search for a buyer’’
Seller enters ‘testBuyer’
Seller selects 2 for ‘Send a new message’
Seller selects ‘Send a message’
Seller enters ‘hello’
Check ‘testBuyer-testSeller.txt’ file to see if message is sent
Test 6 Status: PASSED

Test 7: Buyer blocks Seller
Steps:
Create a Seller
EXIT by pressing ‘X’
Create a Buyer
Buyer selects 2 for ‘Block a user’
Buyer enters ‘testSeller’
Check ‘accounts.txt’ to see if ‘testBuyer’ has ‘testSeller’ in blocked list
Test 7 Status: PASSED

Test 8: Buyer becomes invisible to Seller
Steps:
Create a Seller
EXIT by pressing ‘X’
Create a Buyer
Buyer selects 3 for ‘Become invisible to user’
Buyer enters ‘testSeller’
Check ‘accounts.txt’ to see if ‘testBuyer’ has ‘testSeller’ in invisible list
Test 8 Status: PASSED

Test 9: Seller blocks Buyer
Steps:
Create a Buyer
EXIT by pressing ‘X’
Create a Seller
Seller selects 3 for ‘Block a user’
Seller enters ‘testBuyer’
Check ‘accounts.txt’ to see if ‘testSeller’ has ‘testBuyer’ in blocked list
Test 9 Status: PASSED

Test 10: Seller becomes invisible to Buyer
Steps:
Create a Buyer
EXIT by pressing ‘X’
Create a Seller
Sellerselects 4 for ‘Become invisible to user’
Seller enters ‘testBuyer’
Check ‘accounts.txt’ to see if ‘testSeller’ has ‘testBuyer’ in invisible list
Test 10 Status: PASSED

Test 11: Seller imports ‘.txt’ file
Steps:
Create a Buyer
EXIT by pressing ‘X’
Create ‘dummy.txt’ that says ‘hello’
Create a Seller
Seller selects 1 for ‘Search for a buyer to message’
Seller selects 1 for ‘See list of buyers’
Seller enters ‘testBuyer’
Seller selects 2 for ‘Send a new message’
Seller selects ‘Send a file’
Seller enters ‘dummy.txt’
Check ‘testBuyer-testSeller.txt’ file to see if it contains ‘hello’
Test 11 Status: PASSED

Test 12: Buyer exports ‘.txt’ file
Steps:
Create a Seller
Seller selects 2 for ‘Create a Store’
Seller enters name of store as ‘testSellerStore’
EXIT by pressing ‘X’
Create a Buyer
Buyer selects 1 for ‘Message a Seller’
Buyer selects 2 for ‘Search for a Seller’
Buyer enters ‘testSeller’
Buyer selects 2 for ‘Send a new message’
Buyer selects 1 for ‘Send a message’
Buyer enters ‘hello’
Buyer enters 5 for ‘Export message history’
Check ‘testBuyer-testSeller.csv’ file to see if it contains conversation history
Test 12 Status: PASSED

Test 13: Buyer edits message to Seller
Steps:
Create a Seller
Seller selects 2 for ‘Create a Store’
Seller enters name of store as ‘testSellerStore’
EXIT by pressing ‘X’
Create a Buyer
Buyer selects 1 for ‘Message a Seller’
Buyer selects 2 for ‘Search for a Seller’
Buyer enters ‘testSeller’
Buyer selects 2 for ‘Send a new message’
Buyer selects 1 for ‘Send a message’
Buyer enters ‘hello’
Check ‘testBuyer-testSeller.txt’ file to see if it contains ‘hello’
Buyer enters 3 for ‘Edit a message’
Buyer enters ‘0’ for messageID
Buyer enters ‘new hello’ for edited message
Check ‘testBuyer-testSeller.txt’ file to see if it contains ‘new hello’
Test 13 Status: PASSED

Test 14: Seller deletes message to Buyer
Steps:
Create a Buyer
EXIT by pressing ‘X’
Create a Seller
Seller selects 1 for ‘Search for a buyer to message’
Seller selects 1 for ‘See list of buyers’
Seller enters ‘testBuyer’
Seller selects 2 for ‘Send a new message’
Seller selects ‘Send a message’
Seller enters ‘hello’
Check ‘testBuyer-testSeller.txt’ file to see if message is sent
Seller selects 4 for ‘Delete a message’
Seller enters ‘0’ for messageID
Check ‘testSeller-testBuyer.txt’ file to see if it is empty
Test 14 Status: PASSED

Test 15: Seller creates a Store
Steps:
Create a Seller
Seller selects 2 for ‘Create a store’
Seller enters ‘testSellerStore’
Check ‘accounts.txt’ to see ‘testSeller’ has ‘testSellerStore’ in store list
Test 15 Status: PASSED

Test 16: User does not exist for Login
Steps:
User selects 1 for ‘Login’
User enters ‘testSeller’
User enters ‘passwordSeller’
ERROR message appears with ‘User doesn’t exist’
Test 16 Status: PASSED

Test 17: User registers with comma
Steps:
User selects 2 for ‘Register’
User enters ‘testBuyer@test.com’
User enters ‘test,Buyer’
User enters ‘passwordBuyer’
User enters ‘B’ for Buyer
ERROR message appears with ‘Username may not contain commas’
Test 17 Status: PASSED

Test 18: User registers with existing username
Steps:
Create a Buyer
EXIT by pressing ‘X’
User selects 2 for ‘Register’
User enters ‘testBuyer1@test.com’
User enters ‘testBuyer’
User enters ‘passwordBuyer1’
User enters ‘B’ for Buyer
ERROR message appears with ‘A user with this username already exists’
Test 18 Status: PASSED

Test 19: User logins with incorrect password
Steps: 
Create a Buyer
EXIT by pressing ‘X’
User enters 1 for ‘Login’
User enters ‘testBuyer’ for username
User enters ‘something’ for password
ERROR message appears with ‘Incorrect password for user!’
Test 19 Status: PASSED

Test 20: User does not enter buyer or seller when Registering
Steps:
User selects 2 for ‘Register’
User enters ‘testBuyer@test.com’
User enters ‘testBuyer’
User enters ‘passwordBuyer’
User enters ‘A’ for Buyer/Seller
ERROR message appears with ‘Please type B for buyer or S for seller!’
Test 20 Status: PASSED
