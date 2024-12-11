## 1、插值语法

	用于解析标签体内容
	{{name}}   （）
	data: {
		name: 'sadasd'
	}
	存在Vue实例中的属性都可以被看见
## 2、模板语法

	用于解析标签（包括标签属性、标签体内容、绑定事件）
	v-bind:    可以就简写成   ：

## 3、数据绑定

	v-model:  可以实现双向绑定，但只能绑定拥有输入元素的组件，存在value属性的组件
			相当于v-bind:value
## 4、el和data
    1、Vue实例中，_开头的属性是Vue自己底层在用的，$开头的是给程序员使用的
	2、可以使用Vue实例的prototype的$mount方法，动态的绑定Vue实例和容器
	3、data的两种写法：
		(1)对象式  data: {}
		(2)函数式  data(){ return {} }
## 5、mvvm模型
	M model 模型		（data）
	V view 视图        （html标签代码）
	VM viewmodel 实例对象 （Vue实例）
## 6、Object.defineProperty
	let person = {
		name: 'cej',
		age: '99'
	}
	Object.defineProperty(person,'age',{
		value: 18,
		enumerable: true, //修饰的属性不参与枚举，不能遍历，得加上配置项
		writable: true  //被定义得属性不能被修改，得加上配置项
		configurable: true //不能被删除，得加上配置项
		get: function(){
			return //age被获取的时候调用
		},
		set: function(){
			//age被修改的时候调用
		}
	}),
## 7、理解数据代理
## 8、Vue中得数据代理
	Vue编译后会把data属性，Vue通过一系列操作移到_data中，包括数据劫持
## 9、事件处理
	v-on:click="showInfo(0,$event)" 单击事件,可以简写成@click=
	Vue({
		methods:{
			showInfo(num0,event){
				//event.target,点击目标
			}
		}
	})
## 10、事件修饰符
	@click.prevent //阻止事件默认行为
			.stop  //组织事件冒泡
			.once  //事件只触发一次
			.capture //使用事件得捕获模式
			.self  //只有event.target是当前操作的元素时才触发。 发生冒泡的时候，event.target都是你点击的那个
			.passive //当前事件立即停止，无需等待事件回调返回
## 11、键盘事件
	@keyup
	@keydown
	@keyup.enter //回车事件
			.delete 
			.esc
			.tab  //tab本身具有切走焦点的功能，所以keyup不好用，用keydown
			.space
			.caps-lock
			.......
## 12、事件总结
## 13、姓名案例
## 14、计算属性
## 15、计算属性_简写
## 16、天气案例
## 17、监视属性
## 18、深度监视
## 19、监视的简写形式
## 20、watch对比computed
## 21、绑定class样式
## 22、绑定style形式
## 23、条件渲染
## 24、列表渲染
## 25、key作用与原理
## 26、列表过滤
## 27、列表排序
## 28、更新时的一个问题
## 29、vue监测数据的原理
## 30、Vue.set()方法