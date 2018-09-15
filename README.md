# Memento
A Serialization library for the JVM written in Kotlin
Memento provides a way to convert Java objects to Mementos 
through a MementoAdapter and then writing these mementos to an output stream.
This approach is much more flexible because you can decide what is
serialized and how to reconstruct an object from a Memento.

## An Example
We have a custom JavaFx Gui component that we want to serialize.
```
class UserView(name: String): HBox(), Memorable {
    val nameLabel = Label(name)
    val id = Users.getId(name)
    val idLabel = Label(id)
    init {
        children.addAll(nameLabel, idLabel)
    }
    
    override val mementoAdapter
}
```
With Java Serialization we had a serious problem because
Label doesn't implement Serializable. So we had to make
the label fields transient and let UserView implement Serializable.
With Memento we don't have this problems. We simply create
a MementoAdapter:
```
class UserView
```
