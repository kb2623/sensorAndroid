package org.example.klemen.sensorandroid

@Suppress("unused", "CanBeVal", "MemberVisibilityCanBePrivate", "LiftReturnOrAssignment")
/**
 * Created by klemen on 16.12.2018.
 */
class Queue<T>(list: MutableList<T>){

	private var items: MutableList<T> = list

	fun isEmpty(): Boolean = this.items.isEmpty()

	fun count(): Int = this.items.count()

	override fun toString() = this.items.toString()

	fun enqueue(element: T) {
		this.items.add(element)
	}

	fun dequeue(): T? {
		if (this.isEmpty()){
			return null
		} else {
			return this.items.removeAt(0)
		}
	}

	fun peek(): T? {
		return this.items[0]
	}

	fun toArray(): Array<out Any>? {
		var a = ArrayList<T>(count())
		var i = 0
		while (!items.isEmpty()) a[i] = dequeue()!!
		return a.toArray()
	}
}