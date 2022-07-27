from locust import HttpUser, TaskSet, between

def fooTest(l):
    l.client.get("/")


class UserBehavior(TaskSet):
    tasks = {fooTest: 10}


class WebsiteUser(HttpUser):
    tasks = [UserBehavior]
    wait_time = between(1, 10)

# test :
# locust --host="http://${HOST}:{PORT}" --headless -u "1"