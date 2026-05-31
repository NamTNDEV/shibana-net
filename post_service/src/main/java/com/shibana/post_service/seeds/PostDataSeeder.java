package com.shibana.post_service.seeds;

import com.shibana.post_service.model.enums.PostPrivacyEnum;
import com.shibana.post_service.model.service_command.posts.PostCreationCommand;
import com.shibana.post_service.service.PostCommandService;
import com.shibana.post_service.service.PostQueryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.UUID;

// @Component  ← disabled: seed đã chạy, không cần nữa
@Slf4j
@RequiredArgsConstructor
@Profile("stoped")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostDataSeeder implements ApplicationRunner {
    PostQueryService postQueryService;
    PostCommandService postCommandService;
    Random random = new Random(42); // 42: có nghĩa là mỗi lần chạy sẽ tạo ra cùng một chuỗi số ngẫu nhiên, giúp dữ liệu seed ổn định hơn giữa các lần chạy

    // ─── Danh sách authorId ───────────────────────────────────────────────────

    static List<UUID> AUTHOR_IDS = List.of(
            UUID.fromString("019e6dad-fe54-7c87-afad-c245d6daa0d5"),
            UUID.fromString("019e6db3-8927-7343-9da1-1752a8b4354f"),
            UUID.fromString("019e730a-a04b-7ccc-9da2-7ce9cfcd7206"),
            UUID.fromString("019e7e46-4a20-7810-a507-93ef0b739996"),
            UUID.fromString("019e7e44-9abe-7df7-a6d1-b0a9f1238def"),
            UUID.fromString("019e7e45-3289-78b8-ae50-dd4b1d213fb4"),
            UUID.fromString("019e7e45-567d-7b37-bfd0-677de23894d6"),
            UUID.fromString("019e7e45-6850-7543-b1a5-99d3f5e774a4"),
            UUID.fromString("019e7e46-5c05-7e08-a41c-520a0ff50edc"),
            UUID.fromString("019e7e45-7a9b-7e20-8a95-7fc4e14f6867"),
            UUID.fromString("019e7e45-9600-7efd-b90f-ae63bf84780d"),
            UUID.fromString("019e7e46-71b6-7785-b00d-e83077c08d65"),
            UUID.fromString("019e7e45-ab42-75f7-aa50-fa39199e17f9"),
            UUID.fromString("019e7e45-cc08-7e56-b370-0183db7c76a9"),
            UUID.fromString("019e7e45-e0ab-7287-9fef-f86a4d3c849e"),
            UUID.fromString("019e7e45-f88e-75b6-84b7-f4a5486da7b6"),
            UUID.fromString("019e7e46-0a10-7fe3-bb9c-4bfddfa08c87"),
            UUID.fromString("019e7e46-18f8-7a20-a247-66e81895f880"),
            UUID.fromString("019e7e46-29ff-7c66-a446-ee5ba51977ee"),
            UUID.fromString("019e7e46-3bdd-7a66-8a74-5ebfd4147ee0"),
            UUID.fromString("019e7e46-8e01-7f58-bef1-96ef52eb0f29"),
            UUID.fromString("019e7e46-a239-7d7c-ace3-dea3ef08aabe"),
            UUID.fromString("019e7e46-b588-787e-a35e-a4e83419a471"),
            UUID.fromString("019e7e46-c695-7e10-92c7-6e4abec612d8")
    );

    // ─── Nội dung bài đăng mẫu (đa dạng độ dài, hashtag, emoji) ─────────────

    private static final List<String> POST_CONTENTS = List.of(

            // ── Siêu ngắn (~10–25 ký tự) ──────────────────────────────────────
            "Thứ Hai rồi 😩",

            "Cơm nhà vẫn nhất 🍚❤️",

            "Mưa. Cà phê. Nhạc. Hoàn hảo ☕🎵",

            "Ai ơi chờ tôi với 😭 #trễgiờ",

            "Productive day! 💪✅",

            // ── Ngắn (~50–100 ký tự) ──────────────────────────────────────────
            "Hôm nay thử nấu mì Ý lần đầu, kết quả... ăn được 😂 Lần sau sẽ ngon hơn! #cooking #homemade",

            "Vừa tập gym xong, người đau nhức hết nhưng tinh thần phấn chấn lắm 🏋️‍♂️🔥 #gym #fitness",

            "Chiều nay trời Sài Gòn đẹp hiếm có, không mưa, không nắng gắt 🌤️ Đi dạo thôi! #saigon",

            "3 năm nữa mình sẽ nhìn lại hôm nay và cảm ơn bản thân đã không bỏ cuộc 💪 #motivation",

            "Cái cảm giác merge PR mà không có conflict 🥹✨ Developers sẽ hiểu. #developer #git",

            "Vừa xem xong Interstellar lần thứ 5, vẫn khóc ở đoạn đó 😭🚀 #movie #interstellar",

            "Hôm nay học được: đừng bao giờ push thẳng lên main 😅 #lessonlearned #git #developer",

            // ── Trung bình (~150–300 ký tự) ───────────────────────────────────
            "Nhận ra một điều: những người thực sự giỏi thường rất khiêm tốn. Họ luôn nghĩ mình còn nhiều thứ phải học. Còn những người hay khoe thì thường ngược lại 🤔\n\nDunning-Kruger effect là có thật. #psychology #life",

            "Review sách: \"Đắc Nhân Tâm\" - Dale Carnegie 📖\n\nĐọc lần đầu năm 18 tuổi thấy hay. Đọc lại lúc 25 tuổi thấy sâu sắc hơn nhiều. Cùng một cuốn sách nhưng mỗi giai đoạn đọc lại thấy ý nghĩa khác nhau.\n\nBạn đã đọc cuốn này chưa? #book #reading #selfhelp",

            "Tip nhỏ cho ai đang học code: đừng chỉ xem tutorial rồi gật đầu. Hãy tắt video, mở editor và tự gõ lại từ đầu. Bạn sẽ ngạc nhiên khi thấy mình không nhớ được bao nhiêu đâu 😅\n\nLearning by doing mới thực sự hiệu quả! #coding #programming #tip",

            "Sáng nay chạy bộ 5km, phá kỷ lục cá nhân! 🏃‍♂️⏱️\n\nTháng trước còn thở không nổi sau 1km, giờ chạy 5km chỉ hơi mệt. Consistency is key!\n\n#running #fitness #personalrecord #health",

            "Cà phê sáng + playlist lo-fi + window view = the perfect WFH morning ☕💻🎵\n\nLàm remote 2 năm rồi và mình nghĩ mình không thể quay lại office full-time được nữa 😅\n\n#wfh #remotework #workfromhome #lifestyle",

            "Vừa hoàn thành khoá học Design Thinking online 🎨🧠\n\nMột trong những framework tư duy hay nhất mình từng học. Đặt người dùng vào trung tâm, prototype nhanh, test thường xuyên.\n\nAi đang học UX/Product thì không nên bỏ qua! #design #ux #designthinking",

            "Hà Nội mùa thu mình chỉ ghé một lần nhưng nhớ mãi 🍂🌿\n\nCái se lạnh buổi sáng, hoa sữa thơm nồng, cốc cà phê trứng nóng hổi... Sài Gòn sinh ra nhưng thương Hà Nội mùa thu thật sự.\n\n#hanoi #autumn #travel #vietnam",

            // ── Dài (~350–550 ký tự) ──────────────────────────────────────────
            "5 extension VSCode mình không thể thiếu khi code 💻\n\n1. Prettier — tự động format code, không cần cãi nhau về style nữa\n2. GitLens — xem blame, history từng dòng code siêu tiện\n3. Thunder Client — test API ngay trong VSCode, không cần mở Postman\n4. Error Lens — highlight lỗi inline, không cần hover\n5. TODO Highlight — đánh dấu TODO/FIXME nổi bật\n\nBạn hay dùng extension nào? Comment bên dưới nhé! 👇\n\n#vscode #developer #coding #productivity #webdev",

            "Mình đã thay đổi cách tiêu tiền sau khi đọc cuốn \"The Psychology of Money\" 💰📚\n\nTrước: tiêu hết lương, tháng nào cũng \"hết tiền trước lương\"\nSau: Pay yourself first — để dành 20% ngay khi nhận lương, tiêu phần còn lại\n\nChỉ một thay đổi nhỏ nhưng sau 8 tháng quỹ khẩn cấp của mình đã đủ 3 tháng chi phí sinh hoạt.\n\nBắt đầu muộn vẫn hơn không bắt đầu! 💪\n\n#personalfinance #money #saving #investing #financial",

            "Recap chuyến đi Đà Lạt 3N2Đ vừa rồi 🌿🌧️\n\nNgày 1: Check-in, dạo chợ đêm, thử bánh tráng nướng\nNgày 2: Thuê xe máy tự lái, khám phá các góc sống ảo ít người biết, ăn lẩu bò nhúng dấm tối\nNgày 3: Cà phê sáng nhìn mây, mua mấy túi trà về làm quà\n\nThành thật mà nói: Đà Lạt bây giờ đông khách quá, nhưng nếu chịu khó tránh điểm check-in nổi tiếng thì vẫn tìm được góc yên tĩnh 🌸\n\nAi muốn địa chỉ cụ thể cmt nhé! #dalat #travel #vietnam #weekend",

            "Hành trình học piano của mình sau 1 năm 🎹\n\nTháng 1-2: Học note, tập bài Twinkle Twinkle Little Star cảm thấy xấu hổ với bản thân 😂\nTháng 3-4: Bắt đầu đọc được sheet nhạc, tay trái tay phải vẫn chưa phối hợp được\nTháng 5-6: Chơi được bài đầu tiên hoàn chỉnh, hàng xóm chắc muốn điên\nTháng 7-9: Cảm nhận được nhạc nhiều hơn, tập 30 phút/ngày không còn cảm giác bắt buộc\nTháng 10-12: Chơi được Yiruma – River Flows in You, khóc một mình 🥹\n\nNếu bạn đang nghĩ học nhạc cụ, làm đi! Không bao giờ là muộn cả. #piano #music #learning",

            "Sau 3 năm làm product, đây là những điều mình ước mình biết sớm hơn 🧠\n\n→ Data nói thật hơn user nói. Đừng tin 100% vào những gì user phản hồi trong interview, hãy xem họ thực sự làm gì.\n\n→ Feature ít hơn không phải là thất bại. Scope nhỏ, ship nhanh, học từ thực tế.\n\n→ \"Done is better than perfect\" — nhưng \"done\" không có nghĩa là cẩu thả. Tìm điểm cân bằng.\n\n→ Mối quan hệ với engineering team quan trọng không kém gì roadmap.\n\nAi đang làm product thì share thêm nhé! 👇 #product #productmanager #startup #tech",

            // ── Dài (~600–750 ký tự) ──────────────────────────────────────────
            "Mình vừa nghỉ việc văn phòng để đi du lịch dài hạn 6 tháng 🌍✈️\n\nNhiều người hỏi: \"Không sợ mất cơ hội nghề nghiệp à?\" Sợ chứ! Nhưng sợ hơn là 30 tuổi nhìn lại thấy mình chỉ sống để đi làm.\n\nItinerary dự kiến:\n📍 Tháng 1-2: Đông Nam Á (Thái, Campuchia, Lào)\n📍 Tháng 3: Nhật Bản mùa hoa anh đào 🌸\n📍 Tháng 4-5: Châu Âu backpack\n📍 Tháng 6: Quay về, reset và tìm việc mới\n\nBudget: ~150 triệu cho 6 tháng (đã tính kỹ)\n\nMình sẽ update hành trình lên đây thường xuyên. Ai có kinh nghiệm du lịch dài hạn thì chia sẻ tip với mình nhé! 🙏\n\n#travel #longtravel #digitalnomad #backpacker #life",

            "Thread dành cho các bạn đang học lập trình và cảm thấy overwhelmed 🧵💻\n\nMình đã từng như vậy. Nhìn roadmap dài vô tận, tutorial hell không lối thoát, học xong quên ngay.\n\nSau 3 năm, đây là những gì mình học được:\n\n🔹 Đừng học nhiều thứ cùng lúc. Chọn 1 ngôn ngữ, 1 framework, đi sâu trước khi mở rộng.\n\n🔹 Project thật > bài tập lý thuyết. Build thứ gì đó bạn thực sự muốn dùng.\n\n🔹 Google và Stack Overflow không phải gian lận. Senior dev cũng dùng mỗi ngày.\n\n🔹 Cộng đồng rất quan trọng. Tìm mentor, tham gia group, đừng học một mình.\n\n🔹 Burnout là thật. Nghỉ ngơi khi cần, không phải yếu đuối.\n\nHãy tag người bạn đang học code cần đọc bài này! 👇\n\n#coding #programming #developer #beginners #webdev",

            "Mình vừa kết thúc 30 ngày no social media và đây là những gì thay đổi 📵\n\nTuần 1: Khó chịu, cứ vô thức mở app rồi... à đã xoá rồi. Lặp lại 20 lần/ngày.\nTuần 2: Bắt đầu quen, đọc sách nhiều hơn, ngủ sớm hơn 1 tiếng.\nTuần 3: Nhận ra mình hay nhìn điện thoại vì lo sợ bỏ lỡ (FOMO), không phải vì thực sự cần.\nTuần 4: Calm hơn rõ rệt. Ít so sánh bản thân với người khác hơn.\n\nKết quả:\n✅ Đọc được 4 cuốn sách\n✅ Tập gym đều 5 buổi/tuần\n✅ Ngủ đủ giấc hơn\n✅ Ít anxiety hơn hẳn\n\nMình có quay lại mạng xã hội không? Có, nhưng có ý thức hơn. Đặt giới hạn 30 phút/ngày.\n\n#digitaldetox #socialmedia #mentalhealth #mindfulness #challenge",

            // ── Rất dài (~800–980 ký tự) ──────────────────────────────────────
            "Tổng hợp những câu hỏi phỏng vấn backend Java mà mình hay gặp nhất 💼☕\n(Lưu lại để ôn nhé!)\n\n❓ Sự khác nhau giữa @Component, @Service, @Repository?\n→ Về bản chất giống nhau, nhưng mang semantic khác nhau và cho phép AOP xử lý riêng biệt.\n\n❓ Giải thích Spring Bean lifecycle?\n→ Instantiation → Populate properties → BeanNameAware → BeanFactoryAware → Pre-init (BeanPostProcessor) → InitializingBean → Custom init → Ready → Destruction\n\n❓ @Transactional hoạt động như thế nào?\n→ Spring tạo proxy, intercept method call, mở transaction trước, commit/rollback sau.\n\n❓ Sự khác nhau giữa EAGER và LAZY loading trong JPA?\n→ EAGER load ngay, LAZY load khi access. Cẩn thận N+1 problem với LAZY.\n\n❓ Cách xử lý N+1 query problem?\n→ Dùng JOIN FETCH, @EntityGraph, hoặc batch fetching.\n\n❓ Circuit Breaker pattern là gì?\n→ Ngăn cascade failure khi service phụ thuộc bị lỗi. Ba trạng thái: Closed, Open, Half-Open.\n\nComment câu hỏi bạn hay bị hỏi thêm nhé! 👇\n\n#java #spring #springboot #interview #backend #developer",

            "Câu chuyện về lần đầu tiên mình deploy lên production và làm sập hệ thống 😱\n\nNăm đó mình mới đi làm được 3 tháng, tự tin vừa fix xong một bug nhỏ. Sếp bảo \"em tự deploy đi cho quen\". Mình hào hứng lắm.\n\nCâu lệnh deploy chạy xong. Xanh hết. Beautiful.\n\n5 phút sau Slack nổ loạn. Production down. 10,000 users không vào được. Hoá ra cái \"bug nhỏ\" đó mình đã vô tình hardcode cái config của local environment lên prod 😭\n\nSếp không la mắng. Chỉ ngồi cạnh, bình tĩnh hỏi: \"Em nghĩ chuyện gì xảy ra?\". Rồi cùng rollback, cùng fix.\n\nBài học mình nhớ đến tận bây giờ:\n→ Luôn double-check config trước khi deploy\n→ Có staging environment để test trước\n→ Rollback plan phải có sẵn\n→ Sai thì nhận, học, không được giấu\n\nMọi dev đều có story kiểu này. Bạn của bạn là gì? 😄\n\n#developer #programming #story #lessonlearned #backend",

            "Review thành thật sau 6 tháng dùng AI tools trong công việc hàng ngày 🤖💼\n\nChatGPT / Claude:\n✅ Viết boilerplate code nhanh hơn 3x\n✅ Giải thích concept phức tạp rất dễ hiểu\n✅ Draft email/document tiết kiệm thời gian\n❌ Hay bịa đặt (hallucinate) khi hỏi số liệu cụ thể\n❌ Không nên tin 100% output, luôn phải verify\n\nGitHub Copilot:\n✅ Autocomplete code thông minh, tiết kiệm ~30% thời gian gõ\n✅ Suggest test case khá tốt\n❌ Đôi khi suggest code không tối ưu hoặc có security issue\n❌ Dễ tạo tâm lý phụ thuộc, junior dev cần cẩn thận\n\nKết luận của mình: AI là công cụ amplify, không phải replace. Người biết dùng AI hiệu quả sẽ có lợi thế rất lớn.\n\nBạn đang dùng AI tool nào trong công việc? 👇\n\n#ai #chatgpt #github #copilot #developer #productivity #tech",

            "Hành trình giảm 10kg trong 4 tháng không nhịn ăn 🏃‍♂️💪\n\nBối cảnh: Sau COVID work from home, mình tăng từ 70kg lên 80kg. Không mặc vừa quần áo cũ, leo 3 tầng cầu thang đã thở dốc.\n\nTháng 1 — Reset:\n→ Đi bộ 30 phút/ngày, không cần chạy\n→ Bỏ nước ngọt, thay bằng nước lọc và trà xanh\n→ Không ăn sau 9h tối\n\nTháng 2 — Build habit:\n→ Tăng lên 45 phút bộ + 15 phút tập nhẹ\n→ Học cơ bản về macro: protein, carb, fat\n→ Giảm cơm trắng 30%, thêm rau xanh\n\nTháng 3-4 — Progressive:\n→ Tập gym 4 buổi/tuần\n→ Tính calo hàng ngày (không cần quá chặt, ±200 calo là ổn)\n→ Ngủ đủ 7-8 tiếng (quan trọng hơn nhiều người nghĩ!)\n\nKết quả: Từ 80kg → 70kg, body fat giảm từ 28% → 19%\n\nNhìn lại, không có bí quyết gì đặc biệt cả — chỉ là consistency và patience! 🙏\n\n#weightloss #fitness #health #gym #transformation"
    );

    // ─── Phân phối số bài mỗi tác giả (tổng = 100 bài) ──────────────────────
    // Lệch nhau có chủ ý: vài user rất active, đa số trung bình, một số ít đăng

    private static final int[] POST_COUNTS_PER_AUTHOR = {
            //  0   1   2   3   4   5   6   7   8   9
            9,  2,  6,  4,  7,  2,  8,  1,  5, 10,
            // 10  11  12  13  14  15  16  17  18  19
            2,  4,  1,  7,  3,  5,  3,  1,  6,  2,
            // 20  21  22  23
            5,  3,  2,  6
    };

    // ─── Privacy distribution (50% PUBLIC / 30% FRIENDS / 20% PRIVATE) ───────

    private static final PostPrivacyEnum[] PRIVACY_POOL = {
            PostPrivacyEnum.PUBLIC, PostPrivacyEnum.PUBLIC, PostPrivacyEnum.PUBLIC,
            PostPrivacyEnum.PUBLIC, PostPrivacyEnum.PUBLIC,
            PostPrivacyEnum.FRIENDS, PostPrivacyEnum.FRIENDS, PostPrivacyEnum.FRIENDS,
            PostPrivacyEnum.PRIVATE, PostPrivacyEnum.PRIVATE
    };


    @Override
    public void run(org.springframework.boot.ApplicationArguments args) throws Exception {
        log.info("=== [PostDataSeeder] Bắt đầu seed dữ liệu bài đăng... ===");

        // Tạo danh sách task: (authorId, privacy, content) theo phân phối lệch
        int totalCreated = 0;

        for (int i = 0; i < AUTHOR_IDS.size(); i++) {
            UUID authorId = AUTHOR_IDS.get(i);
            int count = POST_COUNTS_PER_AUTHOR[i];

            for (int j = 0; j < count; j++) {
                String content = randomContent();
                PostPrivacyEnum privacy = randomPrivacy();

                PostCreationCommand command = new PostCreationCommand(content, authorId, privacy);

                try {
                    postCommandService.createPost(command);
                    totalCreated++;
                } catch (Exception e) {
                    log.error("[PostDataSeeder] Lỗi tạo post cho author {}: {}", authorId, e.getMessage());
                }
            }

            log.debug("[PostDataSeeder] Author {} — đã tạo {} bài", authorId, count);
        }

        log.info("=== [PostDataSeeder] Hoàn thành! Tổng số bài đã tạo: {} ===", totalCreated);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private String randomContent() {
        return POST_CONTENTS.get(random.nextInt(POST_CONTENTS.size()));
    }

    private PostPrivacyEnum randomPrivacy() {
        return PRIVACY_POOL[random.nextInt(PRIVACY_POOL.length)];
    }
}
