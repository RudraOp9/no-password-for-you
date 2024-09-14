function toggleAnswer(questionElement) {
    const answer = questionElement.querySelector('.faq_answer');
    let startTime = null;

	if(answer.style.display === 'block'){
		var crrHeight = answer.style.height;
		var scrollHeight = answer.scrollHeight;
	const interval = setInterval(() => {
							crrHeight = scrollHeight-(scrollHeight * (Date.now() - animationStartTime) / 300) + 'px';

				answer.style.height = crrHeight

				if (Date.now() >= animationStartTime + 300) {
			            clearInterval(interval);
					answer.style.display = 'none'
									answer.style.height = 'auto';


				}
			},5);
		const animationStartTime = Date.now();

	}else{

			answer.style.display = 'block'
			const interval = setInterval(() => {
				answer.style.height = (answer.scrollHeight * (Date.now() - animationStartTime) / 500) + 'px';
				if (Date.now() >= animationStartTime + 500) {
			            clearInterval(interval);

				}
			},5);
		const animationStartTime = Date.now();	
	}
}
fetch('data.json')
    .then(response => response.json())
    .then(data => {
        const dataLength = data.faq.length;
        for (let i = 0; i < dataLength; i++) {

        const container = document.getElementById('layout');
 	const faq = document.createElement('div');
        faq.classList.add('faq');

            const que = document.createElement('div');
	    que.classList.add('faq_question')
            que.innerHTML = `<p>${data.faq[i].que}</p>`;

            const ans = document.createElement('div');
	    ans.classList.add('faq_answer')
            ans.innerHTML = `<p>${data.faq[i].ans}</p>`;
		faq.appendChild(que);
		faq.appendChild(ans);
            container.appendChild(faq);
        }
const divs = document.getElementsByClassName('faq');
for (let i = 0; i < divs.length; i++) {
	divs[i].addEventListener('click',event=>{
		if (event.target.tagName === 'A') {
			return;
		}
		toggleAnswer(divs[i])
	})
}           })
    .catch(error => {
        console.error('Error loading JSON data:', error);
    });


