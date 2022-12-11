Test Cases:
**Clear files in between runs [get rid of ‘.txt’ files and stop and rerun server and client]

Methods:

Create a Seller:
1. User launches an application
2. User presses ‘OK’ on Welcome Message
3. User selects 2 for ‘Register’
4. User enter testBuyer@test.com and presses ‘OK’
5. User enter testBuyer and presses ‘OK’
6. User enter passwordBuyer and presses ‘OK’
7. User enter ‘B’ and presses ‘OK’
8. User gets ‘You are now logged in as testBuyer’

Create a Buyer:
1. User launches an application
2. User presses ‘OK’ on Welcome Message
3. User selects 2 for ‘Register’
4. User enter testSeller@test.com and presses ‘OK’
5. User enter testSeller and presses ‘OK’
6. User enter passwordSeller and presses ‘OK’
7. User enter ‘S and presses ‘OK’
8. User gets ‘You are now logged in as testSeller’

Test 1: User Register
Steps:
1. Create a Buyer
2. Check ‘account.txt’ file for credentials  
Test 1 Status: PASSED

Test 2: User log in
Steps:
1. Create a Seller
2. User launches an application
3. User presses ‘OK’ on Welcome Message
4. User selects 1 for ‘Login’
5. User enter testSeller and presses ‘OK’
6. User enter passwordSeller and presses ‘OK’
7. User gets ‘You are now logged in as testSeller’
8. EXIT by pressing ‘X’  
Test 2 Status: PASSED

Test 3: Buyer messages Seller through Stores
Steps:
1. Create a Seller
2. Seller selects 2 for ‘Create a Store’
3. Seller enters name of store as ‘testSellerStore’
4. EXIT by pressing ‘X’
5. Create a Buyer
6. Buyer selects 1 for ‘Message a Seller’
7. Buyer selects 1 for ‘See a list of Stores’
8. Buyer enters ‘testSellerStore’
9. Buyer selects 2 for ‘Send a new message’
10. Buyer selects 1 for ‘Send a message’
11. Buyer enters ‘hello’
12. EXIT by pressing ‘X’
13. Check ‘testBuyer-testSeller.txt’ file to see if message is sent  
Test 3 Status: PASSED

Test 4: Buyer messages Seller through List
Steps:
1. Create a Seller
2. Seller selects 2 for ‘Create a Store’
3. Seller enters name of store as ‘testSellerStore’
4. EXIT by pressing ‘X’
5. Create a Buyer
6. Buyer selects 1 for ‘Message a Seller’
7. Buyer selects 2 for ‘Search for a Seller’
8. Buyer enters ‘testSeller’
9.Buyer selects 2 for ‘Send a new message’
10. Buyer selects 1 for ‘Send a message’
11. Buyer enters ‘hello’
12. EXIT by pressing ‘X’
13. Check ‘testBuyer-testSeller.txt’ file to see if message is sent  
Test 4 Status: PASSED

Test 5: Seller messages Buyer through List
Steps:
1. Create a Buyer
2. EXIT by pressing ‘X’
3. Create a Seller
4. Seller selects 1 for ‘Search for a buyer to message’
5. Seller selects 1 for ‘See list of buyers’
6. Seller enters ‘testBuyer’
7. Seller selects 2 for ‘Send a new message’
8. Seller selects ‘Send a message’
9. Seller enters ‘hello’
10. Check ‘testBuyer-testSeller.txt’ file to see if message is sent  
Test 5 Status: PASSED

Test 6: Seller messages Buyer through Search
Steps:
1. Create a Buyer
2. EXIT by pressing ‘X’
3. Create a Seller
4. Seller selects 1 for ‘Search for a buyer to message’
5. Seller selects 2 for ‘Search for a buyer’’
6. Seller enters ‘testBuyer’
7. Seller selects 2 for ‘Send a new message’
8. Seller selects ‘Send a message’
9. Seller enters ‘hello’
10. Check ‘testBuyer-testSeller.txt’ file to see if message is sent  
Test 6 Status: PASSED

Test 7: Buyer blocks Seller
Steps:
1. Create a Seller
2. EXIT by pressing ‘X’
3. Create a Buyer
4. Buyer selects 2 for ‘Block a user’
5. Buyer enters ‘testSeller’
6. Check ‘accounts.txt’ to see if ‘testBuyer’ has ‘testSeller’ in blocked list  
Test 7 Status: PASSED

Test 8: Buyer becomes invisible to Seller
Steps:
1. Create a Seller
2. EXIT by pressing ‘X’
3. Create a Buyer
4. Buyer selects 3 for ‘Become invisible to user’
5. Buyer enters ‘testSeller’
6. Check ‘accounts.txt’ to see if ‘testBuyer’ has ‘testSeller’ in invisible list  
Test 8 Status: PASSED

Test 9: Seller blocks Buyer
Steps:
1. Create a Buyer
2. EXIT by pressing ‘X’
3. Create a Seller
4. Seller selects 3 for ‘Block a user’
5. Seller enters ‘testBuyer’
6. Check ‘accounts.txt’ to see if ‘testSeller’ has ‘testBuyer’ in blocked list  
Test 9 Status: PASSED

Test 10: Seller becomes invisible to Buyer
Steps:
1. Create a Buyer
2. EXIT by pressing ‘X’
3. Create a Seller
4. Sellerselects 4 for ‘Become invisible to user’
5. Seller enters ‘testBuyer’
6. Check ‘accounts.txt’ to see if ‘testSeller’ has ‘testBuyer’ in invisible list  
Test 10 Status: PASSED

Test 11: Seller imports ‘.txt’ file
Steps:
1. Create a Buyer
2. EXIT by pressing ‘X’
3. Create ‘dummy.txt’ that says ‘hello’
4. Create a Seller
5. Seller selects 1 for ‘Search for a buyer to message’
6. Seller selects 1 for ‘See list of buyers’
7. Seller enters ‘testBuyer’
8. Seller selects 2 for ‘Send a new message’
9. Seller selects ‘Send a file’
10. Seller enters ‘dummy.txt’
11. Check ‘testBuyer-testSeller.txt’ file to see if it contains ‘hello’  
Test 11 Status: PASSED

Test 12: Buyer exports ‘.txt’ file
Steps:
1. Create a Seller
2. Seller selects 2 for ‘Create a Store’
3. Seller enters name of store as ‘testSellerStore’
4. EXIT by pressing ‘X’
5. Create a Buyer
6. Buyer selects 1 for ‘Message a Seller’
7. Buyer selects 2 for ‘Search for a Seller’
8. Buyer enters ‘testSeller’
9. Buyer selects 2 for ‘Send a new message’
10. Buyer selects 1 for ‘Send a message’
11. Buyer enters ‘hello’
12. Buyer enters 5 for ‘Export message history’
13. Check ‘testBuyer-testSeller.csv’ file to see if it contains conversation history  
Test 12 Status: PASSED

Test 13: Buyer edits message to Seller
Steps:
1. Create a Seller
2. Seller selects 2 for ‘Create a Store’
3. Seller enters name of store as ‘testSellerStore’
4. EXIT by pressing ‘X’
5. Create a Buyer
6. Buyer selects 1 for ‘Message a Seller’
7. Buyer selects 2 for ‘Search for a Seller’
8. Buyer enters ‘testSeller’
9. Buyer selects 2 for ‘Send a new message’
10. Buyer selects 1 for ‘Send a message’
11. Buyer enters ‘hello’
12. Check ‘testBuyer-testSeller.txt’ file to see if it contains ‘hello’
13. Buyer enters 3 for ‘Edit a message’
14. Buyer enters ‘0’ for messageID
15. Buyer enters ‘new hello’ for edited message
16. Check ‘testBuyer-testSeller.txt’ file to see if it contains ‘new hello’  
Test 13 Status: PASSED

Test 14: Seller deletes message to Buyer
Steps:
1. Create a Buyer
2. EXIT by pressing ‘X’
3. Create a Seller
4. Seller selects 1 for ‘Search for a buyer to message’
5. Seller selects 1 for ‘See list of buyers’
6. Seller enters ‘testBuyer’
7. Seller selects 2 for ‘Send a new message’
8. Seller selects ‘Send a message’
9.Seller enters ‘hello’
10. Check ‘testBuyer-testSeller.txt’ file to see if message is sent
11. Seller selects 4 for ‘Delete a message’
12. Seller enters ‘0’ for messageID
13. Check ‘testSeller-testBuyer.txt’ file to see if it is empty  
Test 14 Status: PASSED

Test 15: Seller creates a Store
Steps:
1. Create a Seller
2. Seller selects 2 for ‘Create a store’
3. Seller enters ‘testSellerStore’
4. Check ‘accounts.txt’ to see ‘testSeller’ has ‘testSellerStore’ in store list  
Test 15 Status: PASSED

Test 16: User does not exist for Login
Steps:
1. User selects 1 for ‘Login’
2. User enters ‘testSeller’
3. User enters ‘passwordSeller’
4. ERROR message appears with ‘User doesn’t exist’  
Test 16 Status: PASSED

Test 17: User registers with comma
Steps:
1. User selects 2 for ‘Register’
2. User enters ‘testBuyer@test.com’
3. User enters ‘test,Buyer’
4. User enters ‘passwordBuyer’
5. User enters ‘B’ for Buyer
6. ERROR message appears with ‘Username may not contain commas’  
Test 17 Status: PASSED

Test 18: User registers with existing username
Steps:
1. Create a Buyer
2. EXIT by pressing ‘X’
3. User selects 2 for ‘Register’
4. User enters ‘testBuyer1@test.com’
5. User enters ‘testBuyer’
6. User enters ‘passwordBuyer1’
7. User enters ‘B’ for Buyer
8. ERROR message appears with ‘A user with this username already exists’  
Test 18 Status: PASSED

Test 19: User logins with incorrect password
Steps: 
1. Create a Buyer
2. EXIT by pressing ‘X’
3. User enters 1 for ‘Login’
4. User enters ‘testBuyer’ for username
5. User enters ‘something’ for password  
6. ERROR message appears with ‘Incorrect password for user!’  
Test 19 Status: PASSED

Test 20: User does not enter buyer or seller when Registering
Steps:
1. User selects 2 for ‘Register’
2. User enters ‘testBuyer@test.com’
3. User enters ‘testBuyer’
4. User enters ‘passwordBuyer’
5. User enters ‘A’ for Buyer/Seller
6. ERROR message appears with ‘Please type B for buyer or S for seller!’  
Test 20 Status: PASSED
