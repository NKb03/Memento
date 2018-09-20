# Memento
A Serialization library for the JVM written in Kotlin
Memento provides a way to convert Java objects to Mementos 
through a MementoAdapter and then writing these mementos to an output stream.
This approach is much more flexible because you can decide what is
serialized and how to reconstruct an object from a Memento.

## An Example
We have a custom JavaFx Gui component that we want to serialize.
```
class UserView(val name: String): HBox() {
    val nameLabel = Label(name)
    val id = Users.getId(name)
    val idLabel = Label(id)
    init {
        children.addAll(nameLabel, idLabel)
    }
}
```
With Java Serialization we had a serious problem because
Label doesn't implement Serializable. So we had to make
the label fields transient and let UserView implement Serializable.
With Memento we don't have this problems. We simply create
a MementoAdapter and let `UserView`implement `Memorable`:
```
class UserViewMementoAdapter(override val memorized: UserView)
   : MementoAdapter<UserView> {
    var userName by field(UserView::name, memorized)
}

class UserView(val name: String): HBox(), Memorable {
    nameLabel = Label(name)
    val id = Users.getId(name)
    val idLabel = Label(id)
    init {
        children.addAll(nameLabel, idLabel)
    }
    
    override val mementoAdapter get() = UserViewMementoAdapter(this)
    companion object {
        fun createMementoAdapter() = UserViewMementoAdapter(UserView(""))
    }
}
```

Now to convert our UserView to a Memento we need a Memorizer.

`val memorizer = Memorizer.newInstance()`
With this memorizer we can now memorize objects:
```
val uv = UserView("peter")
val memento = memorizer.memorize(uv)
```
`memento` is of type Memento.
Now to get our UserView back we simply use:
```
val oldUV = memorizer.rememeber(memento) as UserView
```
And we have our UserView back.
But how does that work?
When memorizing an object the memorizer gets the mementoAdapter 
val of the memorized object and just gets any property defined
in the MementoAdapter to give it to the returned Memento.
The special `field` delegate just uses the backing field of
a val property to make it mutable.
When the memorizer remembers a memento it searches 
the `createMementoAdapter` function in fhe companion object
of the serialized object and sets all properties to the values
specified in the remembered memento.

## Writing Mementos to an OutputStream
To serialize and deserilalize mementos you just have to call 
`writeTo` and `Memento.readFrom` specifying the OutputStream or InputStream

## Registering custom adapters
Perhaps you have some third-party class that you want to memorize.
With Java-Serialization you had a problem because you can't let
the class implement `Serializable` from your project.
With memento you just get the adapter registrar of your memorizer
and register a custom adapter.
```
val memorizer = Memorizer.newInstance()
val registrar = memorizer.adapterRegistrar
registrar.registerAdapter<ThirdPartyClass, YourAdapter>()
```

## Directly memorizing
Sometimes it is tedious to write a special memento adapter
for your class if all you want is to memorize all properties.
Then you can let your class implement `SelfMemorable`

data class Record(val id: String, val name: String, val age: Int): SelfMemorabld



memementos
