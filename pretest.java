Clean Code Pre Test

1) what is the output of the following code     
class CA
{
  public void fun()
  {
    print(“CA”);
  }
}

class CB extends CA
{
  public void fun()
  {
    print(“CB”)
  }
}

void main()
{
  CA a = new CB(); 
  a.fun();
}

1) CA.  	2) CB 	3) ERROR

2) what is the output of the following code

class CA
{
public void fun()
{
print(“CA”);
}
}

class CB extends CA
{
public void fun()
{
print(“CB”)
}
}

void DoJob(CA obj)
{
obj.fun()
}

void main()
{
CA a = new CB();
DoJob(a);
}

1) CA.  	2) CB 	3) ERROR

3) what is the output of the following code

class CA
{
}
class CB
{
}
class Util
{
public static void fun(CA a)
{
print(“CA”);
}
public static void fun(CB a)
{
print(“CB”);
}
}

void main()
{
CA a = new CB();
Util.fun(a);
}

1) CA.  	2) CB 	3) ERROR

4) what is the output of the following code

class CA
{
}
class CB extends CA
{
}

class CX
{
public void fun(CA a)
{
print(“CX CA”);
}
public void fun(CB a)
{
print(“CX CB”);
}
}
class CY extends CX
{
public void fun(CA a)
{
print(“CY CA”);
}
public void fun(CB a)
{
print(“CY CB”);
}
}
void main()
{
CA a = new CB(); CX x = new CY(); x.fun(a);
}

CX CA.  	2) CY CA	3) CY CB

5) what is the output of the following code

var data = [1, 2, 3, 4, 5];
var numbers = data.map(function (nr) { return nr + 1;
});
foreach(number in numbers)
{

print(number);
}

 2 3 4 5 6  	2) 1 2 3 4 5 6	3) error
