const para = document.querySelector('p');

para.addEventListener('click', updateName);

function updateName() {
    let name = prompt('输入一个新的名字：');
    para.textContent = '玩家 1：' + name;
}
const buttons = document.querySelectorAll('button');

for(let i = 0; i < buttons.length ; i++) {
    buttons[i].addEventListener('click', createParagraph);
}
